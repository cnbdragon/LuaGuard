/*
 * Copyright 2014 jwulf.
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
package luaguard.commandLine;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.internal.Lists;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jwulf
 */
public class JCommanderLuaGuard {

    @Parameter
    private List<String> paramaters = new ArrayList<String>();

    @Parameter(names = {"-v","-ver","-version"},
            descriptionKey = "version"/*,
     required = true*/)
    private Integer version;
    //this allows us to guard certain obfuscators.
    
    @Parameter(names = {"-?", "--help"},
            descriptionKey = "help",
            help = true)
    private boolean help;

    @Parameter(names = {"-!", "--about"},
            descriptionKey = "about",
            help = true)
    private boolean about;
    
    @Parameter(names = {"-file", "-files"}, 
            variableArity = true, 
            required = true, 
            /*validateWith = FileValidator.class, */
            descriptionKey = "file")
    private List<String> files;

    @Parameter(names = {"-out","-output"}, 
            variableArity = true,
            descriptionKey = "output")
    private List<String> outputfiles = Lists.newArrayList();

    @Parameter(names = "-blacklist", 
            descriptionKey = "blacklist")
    private List<String> blacklist;

    @Parameter(names = {"-o", "-obfu", "-obfuscator"}, 
            required = true, 
            descriptionKey = "obfuscator")
    private List<String> obfus;
    
    @Parameter(names = {"-f", "-force"},  
            descriptionKey = "force")
    private boolean force;
    
    @Parameter(names = "-debug", 
            descriptionKey = "debug")
    private boolean debug = false;
    
    @ParametersDelegate
        public DelegateLog delegate = new DelegateLog();
    
    public List<String> getfiles(){
        return files;
    } 
    public List<String> getOutput(){
        return outputfiles;
    }
    public List<String> getBlacklist(){
        return blacklist;
    }
    public List<String> getObfuscators(){
        return obfus;
    }
    public boolean getForce(){
        return force;
    }
    public int getLog(){
        return delegate.getL();
    }
    public boolean getHelp() {
        return help;
    }
    public boolean getAbout() {
        return about;
    }
    public int getVersion() {
        return version;
    }
    public boolean getDebug() {
        return debug;
    }
}
