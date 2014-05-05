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
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Stein
 */
public class FunctionParameterObfuscator extends Obfuscator {
    
    final Logger logger = LogManager.getLogger("GLOBAL");
        
    /**
     * Modifies the function body such that all parameters are varargs.
     * Starting statements are select function calls
     * 
     * @param n FuncBody node
     */
    @Override
    public void visit(FuncBody n) {
        
        // No parameters to obfuscate
        if (null == n.parlist.names || n.parlist.isvararg) return;
        
        // Add select statements
        List<Stat> selectStats = new ArrayList<Stat>();
        for (int i = 0; i < n.parlist.names.size(); i++) {
            List<Name> args = new ArrayList<Name>();
            args.add((Name)n.parlist.names.get(i));
            
            // Assignment argument
            List<Exp> assignExps = new ArrayList<Exp>();
            
            // Select function arguments
            List<Exp> funcExps = new ArrayList<Exp>();
            funcExps.add(Exp.numberconstant(Integer.toString(i+1)));
            funcExps.add(Exp.nameprefix("..."));
            
            // Produce select function call
            assignExps.add(Exp.functionop(Exp.nameprefix("select"),
                           FuncArgs.explist(funcExps)));
            
            // Add select assignment to statements list
            selectStats.add(Stat.localassignment(args, assignExps));
        }
        
        // Add selects and change to varargs
        n.block.stats.addAll(0, selectStats);
        n.parlist = new ParList(ParList.EMPTY_NAMELIST, true);
        
        // Recurse
        n.block.accept(this);
    }   
}