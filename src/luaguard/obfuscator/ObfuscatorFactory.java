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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Stein
 */
public class ObfuscatorFactory {
    
    final Logger logger = LoggerFactory.getLogger(ObfuscatorFactory.class);
    
    public ObfuscatorFactory() {}
    
    /**
     * Constructs an obfuscator object given the obfuscator's name
     * 
     * @param name Name of the obfuscation to perform
     * @return Obfuscator object
     */
    public Obfuscator constructObfuscator(String name) {
        
        Obfuscator obf = null;
        if (name.equalsIgnoreCase("fpo")) {
            obf = new FunctionParameterObfuscator();
        }
        
        return obf;
        
    }
}
