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
import java.util.List;
import java.util.Set;
import luaguard.traversal.NameVisitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.Exp.VarargsExp;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Stat.Assign;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.Return;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 *
 * @author Joshua Stein
 */
public class FunctionParameterObfuscator extends Obfuscator {

    final Logger logger = LogManager.getLogger("GLOBAL");

    private Set<String> blacklist;
    
    public FunctionParameterObfuscator() {
        super();
        this.blacklist = new HashSet<String>();
    }
    
    public FunctionParameterObfuscator(List<String> blacklist) {
        super();
        this.blacklist = new HashSet<String>();
        for (String name : blacklist) {
            this.blacklist.add(name);
        }
    }
    
    /**
     * Determine if function is in the blacklist
     * @param n FuncDef node
     */
    @Override
    public void visit(FuncDef n) {
        String name = NameVisitor.funcName(n.name);
        if (!blacklist.contains(name))
            n.body.accept(this);
    }
    
    /**
     * Determine if the anonymous function is in the blacklist
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
     * Modifies the function body such that all parameters are varargs. Starting
     * statements are select function calls
     *
     * @param n FuncBody node
     */
    @Override
    public void visit(FuncBody n) {

        // No parameters to obfuscate
        if (null == n.parlist.names) {
            n.parlist.names = ParList.EMPTY_NAMELIST;
        }

        // Add select statements
        List<Stat> selectStats = new ArrayList<Stat>();
        int adjustNBy = 0;
        for (int i = 0; i < n.parlist.names.size(); i++) {
            List<Name> args = new ArrayList<Name>();
            args.add((Name) n.parlist.names.get(i));

            // Assignment argument
            List<Exp> assignExps = new ArrayList<Exp>();

            if (n.parlist.isvararg) {
                // Remove non-vararg arguments from arg table
                List<Exp> funcExps = new ArrayList<Exp>();
                funcExps.add(Exp.nameprefix("arg"));
                funcExps.add(Exp.numberconstant(Integer.toString(1)));
                assignExps.add(Exp.functionop(Exp.fieldop(Exp.nameprefix("table"), "remove"),
                        FuncArgs.explist(funcExps)));
                adjustNBy++;
            } else {
                // Index arg table (implicit vararg table)
                assignExps.add(Exp.indexop(Exp.nameprefix("arg"),
                        Exp.numberconstant(Integer.toString(i + 1))));
            }

            // Add assignments to statements list
            selectStats.add(Stat.localassignment(args, assignExps));
        }

        if (adjustNBy > 0) {
            // Build Operation: arg.n = arg.n - adjustNum
            Exp.VarExp argField = Exp.fieldop(Exp.nameprefix("arg"), "n");
            Exp argOp = Exp.binaryexp(argField, Lua.OP_SUB, Exp.constant(LuaValue.valueOf(adjustNBy)));
            
            // Add statement to block
            List<Exp.VarExp> vars = new ArrayList<Exp.VarExp>();
            vars.add(argField);
            List<Exp> ops = new ArrayList<Exp>();
            ops.add(argOp);
            selectStats.add(Stat.assignment(vars, ops));
        }        
        
        // Add index/remove assignments and change to varargs
        n.block.stats.addAll(0, selectStats);
        n.parlist = new ParList(ParList.EMPTY_NAMELIST, true);

        // Recurse
        n.block.accept(this);
    }

    @Override
    public void visit(FuncArgs n) {
        unpackVarargs(n.exps);
    }
    
    @Override
    public void visit(Return n) {
        unpackVarargs(n.values);
    }
     
    private void unpackVarargs(List<Exp> lst) {
        if (null != lst) {
            int length = lst.size();
            for (int i = 0; i < length; i++) {
                if (VarargsExp.class.isInstance(lst.get(i))) {
                    NameExp unpackExp = Exp.nameprefix("unpack");
                    List<Exp> funcArgList = new ArrayList<Exp>();
                    funcArgList.add(Exp.nameprefix("arg"));
                    funcArgList.add(Exp.constant(LuaValue.valueOf(1)));
                    funcArgList.add(Exp.fieldop(Exp.nameprefix("arg"), "n"));
                    FuncArgs fArgs = new FuncArgs(funcArgList);
                    lst.set(i, Exp.functionop(unpackExp, fArgs));
                }
            }
        }
    }
}
