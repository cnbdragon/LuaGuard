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
package luaguard.traversal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Visitor;

/**
 * Collects the number of values returned by functions.
 *
 * @author jgs
 */
public class FuncReturnVisitor extends Visitor {

    private Map<String, Integer> funcReturnSize;
    private Deque<String> func;

    public FuncReturnVisitor() {
        funcReturnSize = new HashMap<String, Integer>();
        func = new ArrayDeque<String>();
    }
    
    public Map<String, Integer> getFuncReturnSize() {
        return funcReturnSize;
    }

    @Override
    public void visit(Stat.FuncDef n) {
        String name = NameVisitor.funcName(n.name);

        func.push(name);
        n.body.accept(this);
        func.pop();
    }

    @Override
    public void visit(Stat.Return n) {
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
    public void visit(Stat.Assign n) {
        for (int i = 0; i < n.exps.size(); i++) {
            if (Exp.AnonFuncDef.class.equals(n.exps.get(i).getClass())) {
                Exp.AnonFuncDef fDef = (Exp.AnonFuncDef) n.exps.get(i);
                func.push(((Name) n.vars.get(i)).name);
                fDef.accept(this);
                func.pop();
            }
        }
    }
}
