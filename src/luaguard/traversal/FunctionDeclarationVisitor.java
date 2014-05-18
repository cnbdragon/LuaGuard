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

import java.util.HashMap;
import java.util.Map;
import org.luaj.vm2.ast.Exp.AnonFuncDef;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat.Assign;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Visitor;

/**
 *
 * @author jgs
 */
public class FunctionDeclarationVisitor extends Visitor {
    
    public Map<String, ParList> funcPar;
    
    public FunctionDeclarationVisitor() {
        funcPar = new HashMap<String, ParList>();
    }
    
    /**
     * Adds a function definition's parameters to the dictionary.
     * 
     * @param n FuncDef node
     */
    @Override
    public void visit(FuncDef n) {
        String name = NameVisitor.funcName(n.name);
        
        // Add to map
        funcPar.put(name, new ParList(n.body.parlist.names, n.body.parlist.isvararg));
        
    }
    
    /**
     * Adds an anonymous function declaration's parameters that are assign to variables to the dictionary.
     * 
     * @param n Assign node
     */
    @Override
    public void visit(Assign n) {
        for (int i = 0; i < Math.min(n.vars.size(), n.exps.size()); i++) {
            if (AnonFuncDef.class.equals(n.exps.get(i).getClass())) {
                AnonFuncDef fPar = (AnonFuncDef)n.exps.get(i);
                funcPar.put(((Name)n.vars.get(i)).name, new ParList(fPar.body.parlist.names, fPar.body.parlist.isvararg));
            }
        }
    }
}
