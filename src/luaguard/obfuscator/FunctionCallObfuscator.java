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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.FieldExp;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.IndexExp;
import org.luaj.vm2.ast.Exp.MethodCall;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Visitor;

/**
 *
 * @author jgs
 */
public class FunctionCallObfuscator extends NameResolver {

    /**
     * Visits all NameExp nodes in order to construct the name of a function.
     */
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

    /**
     * Visits all arguments in a function and collects the names of variables used.
     */
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
    private Map<String, ParList> fPar;
    private Random rnd;
    
    public FunctionCallObfuscator() {
        this.blacklist = new HashSet<String>();
        this.blacklist.add("print");
        this.fPar = new HashMap<String, ParList>();
        this.rnd = new Random();
    }
    
    public FunctionCallObfuscator(Set<String> blacklist, Map<String, ParList> fPar) {
        this.blacklist = blacklist;
        this.fPar = fPar;
        this.rnd = new Random();
    }
    
    public FunctionCallObfuscator(Random rnd, Map<String, ParList> fPar) {
        this.blacklist = new HashSet<String>();
        this.blacklist.add("print");
        this.fPar = fPar;
        this.rnd = rnd;
    }
    
    public FunctionCallObfuscator(Set<String> blacklist, Random rnd) {
        this.blacklist = blacklist;
        this.rnd = rnd;
    }
    
    /**
     * Ignore functions in the blacklist 
     * and function calls with fewer arguments than parameters or functions with varargs.
     * 
     * @param n MethodCall node
     */
    @Override
    public void visit(MethodCall n) {
        
        // Check function parameters
        if (fPar.containsKey(n.name)) {
            ParList p = fPar.get(n.name);
            if ((null != p.names && null != n.args.exps && n.args.exps.size() < p.names.size()) 
                    || p.isvararg)
                return;
        }
        
        // Check blacklist
        if (blacklist.contains(n.name)) {
            return;
        }
        
        n.args.accept(this);
    }
    
    /**
     * Ignore functions in the blacklist 
     * and function calls with fewer arguments than parameters or functions with varargs.
     * 
     * @param n FuncCall node
     */
    @Override
    public void visit(FuncCall n) {
        NameVisitor nv = new NameVisitor();
        n.lhs.accept(nv);
        
        // Check function parameters
        if (fPar.containsKey(nv.name)) {
            ParList p = fPar.get(nv.name);
            if ((null != p.names && null != n.args.exps && n.args.exps.size() < p.names.size()) 
                    || p.isvararg)
                return;
        }
        
        // Check blacklist
        if (blacklist.contains(nv.name)) {
            return;
        }
        
        n.args.accept(this);
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
        for (Object var : scope.namedVariables.keySet()) {
            if (!av.isVarPassed(var.toString()) && rnd.nextBoolean()) {
                n.exps.add(Exp.nameprefix(var.toString()));
                break;
            }
        }
    }   
}