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
package luagaurd.commandLine;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.List;

/**
 *
 * @author jwulf
 */
@Parameters(commandDescription = "Obfuscate code in a single file")
public class CommandObfu {

    @Parameter(names = "-file", description = "The file to Obfuscate", required = true/*, validateWith = FileValidator.class*/)
    private List<String> files;

    @Parameter(names = "-output", description = "Name of the output file", required = true)
    private List<String> outputfiles;

    @Parameter(names = "-blacklist", description = "Words to not obfuscate")
    private List<String> blacklist;

    @Parameter(names = "--amend", description = "Amend", hidden = true)
    private Boolean amend = false;

    @Parameter(names = "--author", hidden = true)
    private String author;

    @Parameter(names = {"-o", "-obfu", "-obfuscator"}, description = "Obfuscator to apply to the code.", required = true)
    private List<String> obfus;

    
    List<String> getfiles(){
        return files;
    }
    
    List<String> getOutput(){
        return outputfiles;
    }
    
    List<String> getBlacklist(){
        return blacklist;
    }
    
    List<String> getObfuscators(){
        return obfus;
    }
}
