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
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Visitor;

/**
 * Collects the number of variables that appear on the left side of an
 * assignment.
 *
 * @author jgs
 */
public class FuncUsageVisitor extends Visitor {

    public Map<String, Integer> funcUsageSize;

    public FuncUsageVisitor() {
        funcUsageSize = new HashMap<String, Integer>();
    }
    
    public Map<String, Integer> getFuncUsageSize() {
        return funcUsageSize;
    }

    @Override
    public void visit(Stat.Assign n) {
        if (1 == n.exps.size()) {
            String name = null;

            // Get function name
            if (Exp.FuncCall.class.equals(n.exps.get(0).getClass())) {
                NameVisitor nv = new NameVisitor();
                ((Exp.FuncCall) n.exps.get(0)).lhs.accept(nv);
                name = nv.getName();
            } else if (Exp.MethodCall.class.equals(n.exps.get(0).getClass())) {
                name = ((Exp.MethodCall) n.exps.get(0)).name;
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
