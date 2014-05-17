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
import java.util.Map;
import java.util.Random;
import java.util.Set;
import luaguard.traversal.NameVisitor;
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

    /**
     * Collects the number of variables that appear on the left side of an
     * assignment
     */
    private static class FuncUsageVisitor extends Visitor {
        
        public Map<String, Integer> funcUsageSize;
        
        public FuncUsageVisitor() {
            funcUsageSize = new HashMap<String, Integer>();
        }
        
        @Override
        public void visit(Assign n) {
            if (1 == n.exps.size()) {
                String name = null;

                // Get function name
                if (FuncCall.class.equals(n.exps.get(0).getClass())) {
                    NameVisitor nv = new NameVisitor();
                    ((FuncCall) n.exps.get(0)).lhs.accept(nv);
                    name = nv.getName();
                } else if (MethodCall.class.equals(n.exps.get(0).getClass())) {
                    name = ((MethodCall) n.exps.get(0)).name;
                }

                // Add function return (usage) to the map
                if (null != name) {
                    int currSize = n.vars.size();
                    if (funcUsageSize.containsKey(name)) {
                        currSize = Math.max(currSize, funcUsageSize.get(name));
                    }
                    funcUsageSize.put(name, currSize);
                }
            }
        }
        
    }

    /**
     * Collects the number of values returned by functions
     */
    private static class FuncReturnVisitor extends Visitor {
        
        public Map<String, Integer> funcReturnSize;
        private Deque<String> func;
        
        public FuncReturnVisitor() {
            funcReturnSize = new HashMap<String, Integer>();
            func = new ArrayDeque<String>();
        }
        
        @Override
        public void visit(FuncDef n) {
            String name = NameVisitor.funcName(n.name);
            
            func.push(name);
            n.body.accept(this);
            func.pop();
        }
        
        @Override
        public void visit(Return n) {
            String name = func.getLast();
            int newReturn = n.nreturns();
            
            if (funcReturnSize.containsKey(name)) {
                int currReturn = funcReturnSize.get(name);
                if (-1 == currReturn || -1 == newReturn) {
                    newReturn = -1;
                } else {
                    newReturn = Math.max(currReturn, newReturn);
                }
            }
            funcReturnSize.put(name, newReturn);
        }
        
        @Override
        public void visit(Assign n) {
            for (int i = 0; i < n.exps.size(); i++) {
                if (AnonFuncDef.class.equals(n.exps.get(i).getClass())) {
                    AnonFuncDef fDef = (AnonFuncDef) n.exps.get(i);
                    func.push(((Name) n.vars.get(i)).name);
                    fDef.accept(this);
                    func.pop();
                }
            }
        }
        
    }

    /**
     * Collects all functions that appear inside function calls
     */
    private static class FuncInFuncVisitor extends Visitor {
        
        private Set<String> names;
        int funcCallNesting;
        
        public FuncInFuncVisitor() {
            names = new HashSet<String>();
            funcCallNesting = 0;
        }
        
        @Override
        public void visit(FuncCall n) {

            // Function call inside function call, add to set
            if (funcCallNesting > 0) {
                NameVisitor nv = new NameVisitor();
                n.lhs.accept(nv);
                names.add(nv.getName());
            }

            // Check for more nested function calls
            funcCallNesting++;
            n.args.accept(this);
            funcCallNesting--;
        }
        
        @Override
        public void visit(MethodCall n) {

            // Function call inside function call, add to set
            if (funcCallNesting > 0) {
                names.add(n.name);
            }

            // Check for more nested function calls
            funcCallNesting++;
            n.args.accept(this);
            funcCallNesting--;
        }
        
    }
    
    /**
     * Collects all variables that are used in a return statement
     */
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
    
    private Set<String> funcsInFunc;
    private Map<String, Integer> funcUsages;
    private Map<String, Integer> funcReturns;
    private Random rnd;
    
    public ReturnValueObfuscator() {
        super();
        this.rnd = new Random();
    }
    
    public ReturnValueObfuscator(Random rnd) {
        super();
        this.rnd = rnd;
    }
    
    @Override
    public void visit(Chunk n) {

        // Determine which functions are used inside function calls
        FuncInFuncVisitor fifv = new FuncInFuncVisitor();
        n.accept(fifv);
        funcsInFunc = fifv.names;

        // Determine how function returns are used
        FuncUsageVisitor fuv = new FuncUsageVisitor();
        n.accept(fuv);
        funcUsages = fuv.funcUsageSize;
        
        // Determine what the function actually returns
        FuncReturnVisitor frv = new FuncReturnVisitor();
        n.accept(frv);
        funcReturns = frv.funcReturnSize;

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
     * 
     * Note: Does NOT add return values to functions with a variable number of returns
     *   or if a function call assignment has more variables on the left than are returned.
     *
     * @param n FuncDef node
     */
    @Override
    public void visit(FuncDef n) {
        String name = NameVisitor.funcName(n.name);
        
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
        if (rv.isVararg) {
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
