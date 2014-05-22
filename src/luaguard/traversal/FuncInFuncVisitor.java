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

import java.util.HashSet;
import java.util.Set;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Visitor;

/**
 * Collects all functions that appear inside function calls.
 *
 * @author jgs
 */
public class FuncInFuncVisitor extends Visitor {

    private Set<String> names;
    private int funcCallNesting;

    public FuncInFuncVisitor() {
        names = new HashSet<String>();
        funcCallNesting = 0;
    }
    
    public Set<String> getNames() {
       return names;
    }

    @Override
    public void visit(Exp.FuncCall n) {

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
    public void visit(Exp.MethodCall n) {

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
