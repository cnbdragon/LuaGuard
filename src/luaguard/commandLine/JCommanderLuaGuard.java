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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jwulf
 */
public class JCommanderLuaGuard {

    @Parameter
    private List<String> paramaters = new ArrayList<String>();

    @Parameter(names = {"-log", "-verbose"},
            description = "Level of verbosity")
    private Integer verbose = 1;

    @Parameter(names = {"-version", "-ver"},
            description = "Version of Lua the code is", 
            descriptionKey = "version"/*,
     required = true*/)
    private Integer version;
    //this allows us to guard certain obfuscators.

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = {"-?", "--help"},
            description = "shows this message",
            help = true)
    private boolean help;

    @Parameter(names = {"-!", "--about"},
            description = "Learn about this program",
            help = true)
    private boolean about;

    public int getVerbose() {
        return verbose;
    }

    public int getLog() {
        return verbose;
    }

    public boolean getDebug() {
        return debug;
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
}
