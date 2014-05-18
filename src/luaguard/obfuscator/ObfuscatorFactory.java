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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.luaj.vm2.ast.ParList;

/**
 *
 * @author Joshua Stein
 */
public class ObfuscatorFactory {
    Logger logger = LogManager.getLogger("GLOBAL");
    
    public ObfuscatorFactory() {}
    
    /**
     * Constructs an obfuscator object given the obfuscator's name
     * 
     * @param name Name of the obfuscation to perform
     * @param funcs
     * @return Obfuscator object
     */
    public Obfuscator constructObfuscator(String name) {
        logger.debug("Construct Obfuscator");
        Obfuscator obf = null;
        if (name.equalsIgnoreCase("none")) {
            obf = new IdentityObfuscator();
            logger.debug("Build none");
        } else if (name.equalsIgnoreCase("fpo")) {
            obf = new FunctionParameterObfuscator();
            logger.debug("Build fpo");
        } else if (name.equalsIgnoreCase("rvo")) {
            obf = new ReturnValueObfuscator();
            logger.debug("Build rvo");
        } else if (name.equalsIgnoreCase("fco")) {
            obf = new FunctionCallObfuscator(new Random());
            logger.debug("Build fco");
        } else if (name.equalsIgnoreCase("vro")) {
            obf = new VarRenamerObfuscator();
            logger.debug("Build vro");
        } else if (name.equalsIgnoreCase("jso")) {
            obf = new JunkStatObfuscator();
            logger.debug("Build jso");
        } else if (name.equalsIgnoreCase("fro")) {
            obf = new FunRenamerObfuscator();
            logger.debug("Build fro");
        } else if (name.equalsIgnoreCase("ro"))  {
            obf = new RenamerObfuscator();
            logger.debug("Build ro");
        }

        return obf;
        
    }
    
    public List<String> getObfuscatorList(){
        List temp = new LinkedList();
        temp.add("none");
        temp.add("fpo");
        temp.add("rvo");
        temp.add("fco");
        temp.add("jso");
        temp.add("vro");
        temp.add("fro");
        return temp;
    }
}
