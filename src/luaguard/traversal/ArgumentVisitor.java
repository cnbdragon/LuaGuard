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
 * Visits all arguments in a function and collects the names of variables used.
 *
 * @author jgs
 */
public class ArgumentVisitor extends Visitor {

    private Set<String> names;

    public ArgumentVisitor() {
        names = new HashSet<String>();
    }

    @Override
    public void visit(Exp.NameExp n) {
        names.add(n.name.name);
    }

    public boolean isVarPassed(String name) {
        return names.contains(name);
    }
}
