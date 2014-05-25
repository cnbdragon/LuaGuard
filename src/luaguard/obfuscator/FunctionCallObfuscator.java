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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import luaguard.traversal.ArgumentVisitor;
import luaguard.traversal.FunctionDeclarationVisitor;
import luaguard.traversal.NameVisitor;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.MethodCall;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.ParList;

/**
 *
 * @author jgs
 */
public class FunctionCallObfuscator extends NameResolver {
    
    private Set<String> blacklist;
    private Map<String, ParList> fPar;
    private Random rnd;
    
    public FunctionCallObfuscator() {
        super();
        this.blacklist = new HashSet<String>();
        this.fPar = new HashMap<String, ParList>();
        this.rnd = new Random();
    }

    public FunctionCallObfuscator(Random rnd) {
        super();
        this.blacklist = new HashSet<String>();
        this.fPar = new HashMap<String, ParList>();
        this.rnd = rnd;
    }
    
    public FunctionCallObfuscator(List<String> blacklist) {
        super();
        this.blacklist = new HashSet<String>();
        this.fPar = new HashMap<String, ParList>();
        this.rnd = new Random();
        for (String name : blacklist) {
            this.blacklist.add(name);
        }
    }
    
    public FunctionCallObfuscator(Random rnd, List<String> blacklist) {
        super();
        this.blacklist = new HashSet<String>();
        this.fPar = new HashMap<String, ParList>();
        this.rnd = rnd;
        for (String name : blacklist) {
            this.blacklist.add(name);
        }
    }
    
    @Override
    public void visit(Chunk n) {
        
        // Collect function parameters
        FunctionDeclarationVisitor fdv = new FunctionDeclarationVisitor();
        n.block.accept(fdv);
        fPar = fdv.funcPar;
        
        // Modify the remainder of the code
        n.block.accept(this);
    }
    
    /**
     * Ignore functions in the blacklist 
     * and function calls with fewer arguments than parameters or functions with varargs.
     * 
     * @param n MethodCall node
     */
    @Override
    public void visit(MethodCall n) {
        
        if (isChangePermitted(n.name, n.args)) {
            n.args.accept(this);
        }
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
        
        if (isChangePermitted(nv.getName(), n.args)) {
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
        for (Object var : scope.namedVariables.keySet()) {
            if (!av.isVarPassed(var.toString()) && rnd.nextBoolean()) {
                n.exps.add(Exp.nameprefix(var.toString()));
                break;
            }
        }
    }
    
    private boolean isChangePermitted(String name, FuncArgs args) {
                // If the function parameters are unknown or function is blacklisted, then stop
        if (!fPar.containsKey(name) || blacklist.contains(name)) {
            return false;
        }
        
        ParList p = fPar.get(name);
        
        // If the function is varargs or function call has fewer args than the prototype, then stop
        if ((null != p.names && null != args.exps && args.exps.size() < p.names.size()) 
            || p.isvararg
            || (null == args.exps && null != p.names))
            return false;
        
        return true;
    }
}
