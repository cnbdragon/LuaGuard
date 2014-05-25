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
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Visitor;

/**
 * Collects all variables that are used in a return statement.
 *
 * @author jgs
 */
public class ReturnVisitor extends Visitor {

    private Set<String> names;
    private boolean isVararg;

    public ReturnVisitor() {
        names = new HashSet<String>();
        isVararg = false;
    }

    @Override
    public void visit(Stat.Return n) {
        if (n.nreturns() == -1) {
            isVararg = true;
            return;
        }
        super.visit(n);
    }

    @Override
    public void visit(Exp.NameExp n) {
        names.add(n.name.name);
    }

    public boolean getIsVararg() {
        return isVararg;
    }

    public boolean isVarReturned(String name) {
        return names.contains(name);
    }
}
