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
            description = "Version of Lua the code is", 
            descriptionKey = "version"/*,
     required = true*/)
    private Integer version;
    //this allows us to guard certain obfuscators.
    
    @Parameter(names = {"-?", "--help"},
            description = "shows this message",
            help = true)
    private boolean help;

    @Parameter(names = {"-!", "--about"},
            description = "Learn about this program",
            help = true)
    private boolean about;
    
    @ParametersDelegate
        private Delegate delegate = new Delegate();

    public boolean getHelp() {
        return help;
    }
    public boolean getAbout() {
        return about;
    }
    public int getVersion() {
        return version;
    }
    public int getLog(){
        return delegate.getL();
    }
}
