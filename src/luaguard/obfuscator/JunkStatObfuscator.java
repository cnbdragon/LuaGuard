/*
 * Copyright 2014 Yafei Yao, Jianbin Feng, Jinke Peng.
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

import java.util.List;
import org.luaj.vm2.ast.Block;
import org.luaj.vm2.ast.Stat;

/**
 * Add junk code to the source code Junk code is from the source code
 */
public class JunkStatObfuscator extends Obfuscator {

    @Override
    public void visit(Block b) {

        // Get the statement list of source code
        if (b.stats == null) {
            return;
        }

        List<Stat> l = b.stats;

        // Find assignment and add to end
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).getClass().getName().contains("Assign")) {
                Stat old = l.get(i);
                b.add(old);
                break;

            }
        }
    }
}
