/*
 * Copyright 2014 Joshua Stein.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package luaguard.obfuscator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.MethodCall;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.Stat.Return;
import org.luaj.vm2.ast.Visitor;

/**
 *
 * @author jgs
 */
public class ReturnValueObfuscator extends NameResolver {
    
    private static class ReturnVisitor extends Visitor {
        
        private Set<String> names;
        private boolean isVararg;
        
        public ReturnVisitor() {
            names = new HashSet<String>();
            isVararg = false;
        }
        
        @Override
        public void visit(Return n) {
            if (n.nreturns() == -1) {
                isVararg = true;
                return;
            }
            super.visit(n);
        }
        
        @Override
        public void visit(NameExp n) {
           names.add(n.name.name);
        }
        
        public boolean isVararg() {
            return isVararg;
        }
        
        public boolean isVarReturned(String name) {
            return names.contains(name);
        }
    
    }
    
    /**
     * Ignore Function call names
     * 
     * @param n FuncCall node
     */
    @Override
    public void visit(FuncCall n) {
    }
    
    /**
     * Ignore Method call names
     * 
     * @param n MethodCall node
     */
    @Override
    public void visit(MethodCall n) {
    }
    
    /**
     * Adds return statement to a function if the last statement is not a return.
     * i.e. turns void functions into non-void
     * 
     * @param n FuncBody node
     */
    @Override
    public void visit(FuncBody n) {
        if (!Return.class.isInstance(n.block.stats.get(n.block.stats.size()-1))) {
            n.block.add(new Return(null));
        }
        super.visit(n);
    }
    
    /**
     * Add variables to return statement that are not already returned.
     * Does not change return values for vararg returns.
     * 
     * @param n Return node
     */
    @Override
    public void visit(Return n) {
        ReturnVisitor rv = new ReturnVisitor();
        n.accept(rv);
        
        // Vararg return: do not want to risk changing behaviour
        if (rv.isVararg) return;
        
        // Make void functions return
        if (null == n.values) {
            n.values = new ArrayList();
        }
        
        // Add variable to the return statement that is not already returned
        for (Object var : scope.namedVariables.keySet()) {
            if (!rv.isVarReturned(var.toString())) {
                n.values.add(Exp.nameprefix(var.toString()));
                break;
            }
        }
    }
    
}
