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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import luaguard.traversal.FuncInFuncVisitor;
import luaguard.traversal.FuncReturnVisitor;
import luaguard.traversal.FuncUsageVisitor;
import luaguard.traversal.NameVisitor;
import luaguard.traversal.ReturnVisitor;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.AnonFuncDef;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.MethodCall;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.NameScope;
import org.luaj.vm2.ast.Stat.Assign;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.Return;
import org.luaj.vm2.ast.Visitor;

/**
 *
 * @author jgs
 */
public class ReturnValueObfuscator extends NameResolver {
    
    private Set<String> funcsInFunc;
    private Map<String, Integer> funcUsages;
    private Map<String, Integer> funcReturns;
    private Set<String> blacklist;
    private Random rnd;
    
    public ReturnValueObfuscator() {
        super();
        this.rnd = new Random();
        this.blacklist = new HashSet<String>();
    }
 
   public ReturnValueObfuscator(Random rnd) {
        super();
        this.rnd = rnd;
        this.blacklist = new HashSet<String>();
    }
    
    public ReturnValueObfuscator(Random rnd, List<String> blacklist) {
        super();
        this.rnd = rnd;
        this.blacklist = new HashSet<String>();
        for (String name : blacklist) {
            this.blacklist.add(name);
        }
    }
    
    @Override
    public void visit(Chunk n) {

        // Determine which functions are used inside function calls
        FuncInFuncVisitor fifv = new FuncInFuncVisitor();
        n.accept(fifv);
        funcsInFunc = fifv.getNames();

        // Determine how function returns are used
        FuncUsageVisitor fuv = new FuncUsageVisitor();
        n.accept(fuv);
        funcUsages = fuv.getFuncUsageSize();
        
        // Determine what the function actually returns
        FuncReturnVisitor frv = new FuncReturnVisitor();
        n.accept(frv);
        funcReturns = frv.getFuncReturnSize();

        // Modify the remainder of the code
        n.block.accept(this);
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
     * Adds return statement to a function if the last statement is not a
     * return. i.e. turns void functions into non-void
     * Used for anonymous functions
     * 
     * Note: Does NOT add return values to functions with a variable number of returns
     *   or if a function call assignment has more variables on the left than are returned.
     *
     * @param n Assign node
     */
    @Override
    public void visit(Assign n) {
        for (int i = 0; i < n.exps.size(); i++) {
            NameVisitor nv = new NameVisitor();
            ((Exp)n.vars.get(i)).accept(nv);
            if (!blacklist.contains(nv.getName()) && Exp.AnonFuncDef.class.equals(n.exps.get(i).getClass())) {
                ((Exp) n.exps.get(i)).accept(this);
            }
        }
    }
    
    /**
     * Adds return statement to a function if the last statement is not a
     * return. i.e. turns void functions into non-void
     * 
     * Note: Does NOT add return values to functions with a variable number of returns
     *   or if a function call assignment has more variables on the left than are returned.
     *
     * @param n FuncDef node
     */
    @Override
    public void visit(FuncDef n) {
        String name = NameVisitor.funcName(n.name);
        
        if (blacklist.contains(name))
            return;
        
        // All functions are given at least one return statement
        if (!Return.class.isInstance(n.body.block.stats.get(n.body.block.stats.size() - 1))) {
            n.body.block.add(new Return(null));
        }
        
        // Function returns are only modified if it is safe 
        // (# on the left of assignment <= # returned by function)
        // Not used within a function call
        if (funcUsages.containsKey(name) && funcReturns.containsKey(name)
                && funcUsages.get(name) <= funcReturns.get(name)
                && funcReturns.get(name) != -1 && !funcsInFunc.contains(name)) {
            n.body.accept(this);
        }
    }
    
    /**
     * Add variables to return statement that are not already returned. Does not
     * change return values for vararg returns.
     *
     * @param n Return node
     */
    @Override
    public void visit(Return n) {
        ReturnVisitor rv = new ReturnVisitor();
        n.accept(rv);

        // Vararg return: do not want to risk changing behaviour
        if (rv.getIsVararg()) {
            return;
        }

        // Make void functions return
        if (null == n.values) {
            n.values = new ArrayList();
        }

        // Find non-empty scope
        NameScope retScope = scope;
        while (retScope != null && retScope.namedVariables.isEmpty()) {
            retScope = retScope.outerScope;
        }
        
        if (retScope != null) {

            // Expand variable set, if possible
            Map namedVars = retScope.namedVariables;
            if (retScope.outerScope != null) {
                namedVars.putAll(retScope.outerScope.namedVariables);
            }

            // Add variable to the return statement that is not already returned
            for (Object var : namedVars.keySet()) {
                if (!rv.isVarReturned(var.toString()) && rnd.nextBoolean()) {
                    n.values.add(Exp.nameprefix(var.toString()));
                    break;
                }
            }
        }
    }
    
}
