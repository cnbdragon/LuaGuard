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

package luaguard;

import luaguard.commandLine.CommandObfuFolder;
import luaguard.commandLine.CommandObfu;
import luaguard.commandLine.ListFilesUtility;
import luaguard.commandLine.JCommanderLuaGuard;
import com.beust.jcommander.JCommander;


/**
 *
 * @author jwulf
 */
public class LuaGuard {

    /**
     * @param argv the command line arguments
     */
    public static void main(String[] argv) {
        // TODO code application logic here
        JCommanderLuaGuard jclg = new JCommanderLuaGuard();
        JCommander mainCommander = new JCommander(jclg/*, argv*/);
        CommandObfu obfu = new CommandObfu();
        CommandObfuFolder obfuFold = new CommandObfuFolder();
        //don't need to load since we can do @file for config
        //CommandConfig config = new CommandConfig(); 
        
        mainCommander.addCommand("-obfuscate", obfu);
        mainCommander.addCommand("-obfuscateFolder", obfuFold);
        //don't need to load since we can do @file for config
        //mainCommander.addCommand("-config", config);
 
        //name of program to print in help
        mainCommander.setProgramName("LuaGuard"); 
        
        
            
        mainCommander.parse(argv);
       

        if(jclg.getHelp()){
            mainCommander.usage();
        } else if(jclg.getAbout()){
            System.out.println("about");
        } else if( true){
            ListFilesUtility files = new ListFilesUtility();
            if(files.exists("c://test2")){
            files.listFiles("C://test2");
            } else {
                System.out.println("Hey, we didn't find the directory");
            }
        }
    }
    
}
