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
import java.util.Random;
import java.util.Set;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.FieldExp;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.IndexExp;
import org.luaj.vm2.ast.Exp.MethodCall;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.Visitor;

/**
 *
 * @author jgs
 */
public class FunctionCallObfuscator extends NameResolver {

        private static class NameVisitor extends Visitor {
            String name;
            
            public NameVisitor() {
                name = "";
            }
            
            @Override
            public void visit(NameExp n) {
                name = n.name.name;
            }
            
            @Override
            public void visit(FieldExp n) {
                name = n.name.name;
            }
            
            @Override
            public void visit(IndexExp n) {}
            
            
        }
    
        private static class ArgumentVisitor extends Visitor {
        
        private Set<String> names;
        
        public ArgumentVisitor() {
            names = new HashSet<String>();
        }
        
        @Override
        public void visit(NameExp n) {
           names.add(n.name.name);
        }
        
        
        public boolean isVarPassed(String name) {
            return names.contains(name);
        }
    }
    
    private Set<String> blacklist;
    
    public FunctionCallObfuscator() {
        this.blacklist = new HashSet<String>();
        this.blacklist.add("print");
    }
    
    public FunctionCallObfuscator(Set<String> blacklist) {
        this.blacklist = blacklist;
    }
    
    /**
     * Ignore functions in the blacklist
     * 
     * @param n MethodCall node
     */
    @Override
    public void visit(MethodCall n) {
        if (!blacklist.contains(n.name)) {
            n.args.accept(this);
        }
    }
    
    /**
     * Ignore functions in the blacklist
     * 
     * @param n FuncCall node
     */
    @Override
    public void visit(FuncCall n) {
        NameVisitor nv = new NameVisitor();
        n.lhs.accept(nv);
        
        if (!blacklist.contains(nv.name)) {
            n.args.accept(this);
        }
    }
    
    /**
     * Add additional arguments to the function call
     * @param n 
     */
    @Override
    public void visit(FuncArgs n) {
        ArgumentVisitor av = new ArgumentVisitor();
        n.accept(av);
        
        // Make function call with no arguments pass arguments
        if (null == n.exps) {
            n.exps = new ArrayList<Exp>();
        }
        
        // Add variable to the return statement that is not already returned
        Random rnd = new Random();
        for (Object var : scope.namedVariables.keySet()) {
            if (!av.isVarPassed(var.toString()) && rnd.nextBoolean()) {
                n.exps.add(Exp.nameprefix(var.toString()));
                break;
            }
        }
    }   
}