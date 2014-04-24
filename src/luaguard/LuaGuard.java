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

import com.beust.jcommander.JCommander;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import luaguard.commandLine.CommandObfu;
import luaguard.commandLine.CommandObfuFolder;
import luaguard.commandLine.JCommanderLuaGuard;
import luaguard.commandLine.ListFilesUtility;
import luaguard.unparser.*;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.*;
import org.luaj.vm2.ast.Exp.*;
import org.luaj.vm2.ast.Exp.AnonFuncDef;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.LocalFuncDef;
import org.luaj.vm2.parser.*;

/**
 *
 * @author jwulf
 */
public class LuaGuard {

    /**
     * @param argv the command line arguments
     */
    public static void main(String[] argv) {

        // resource bundle for internationilization
        //Locale local = new Locale("en", "US");
        
        ResourceBundle hints = ResourceBundle.getBundle("luaguard.i18n.CommandLineHints");
        
        // TODO code application logic here
        JCommanderLuaGuard jclg = new JCommanderLuaGuard();
        JCommander mainCommander = new JCommander(jclg,hints/*, argv*/);
        CommandObfu obfu = new CommandObfu();
        CommandObfuFolder obfuFold = new CommandObfuFolder();
        //don't need to load since we can do @file for config
        //CommandConfig config = new CommandConfig(); 

        mainCommander.addCommand("-obfuscate", obfu);
        //mainCommander.addCommand("-obfuscateFolder", obfuFold);
        //don't need to load since we can do @file for config
        //mainCommander.addCommand("-config", config);

        //name of program to print in help
        mainCommander.setProgramName("LuaGuard");
        //mainCommander.setDescriptionsBundle(hints);

        mainCommander.parse(argv);

        if (jclg.getHelp()) {
            mainCommander.usage();
        } else if (jclg.getAbout()) {
            System.out.println("about");
        } else if (true) {
//            ListFilesUtility files = new ListFilesUtility();
//            if (files.exists("c://test2")) {
//                files.listFiles("C://test2");
//            } else {
//                System.out.println("Hey, we didn't find the directory");
//            }


            /*
             * lua parser
             */
            List<String> files = obfu.getfiles();
            String file = files.get(0);
            List<String> outputs = obfu.getOutput();
            PrintStream out;

            try {
                if (!outputs.isEmpty()) {
                    String output = outputs.get(0);
                    out = new PrintStream(output);
                } else {
                    out = System.out;
                }
                LuaParser parser = new LuaParser(new FileInputStream(file));
                Chunk chunk = parser.Chunk(); // this parses the file
                
                //Visitor visit = new LuaUnparser(System.out);
                /* this is were we would put the obfuscators.
                * this will probably be a loop structure of some type.
                */
                
                chunk.accept(new LuaUnparser(out, false)); //this unparses the file

            } catch (FileNotFoundException | ParseException | SecurityException ex) {
                Logger.getLogger(LuaGuard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
