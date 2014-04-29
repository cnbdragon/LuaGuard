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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import luaguard.commandLine.JCommanderLuaGuard;
import luaguard.commandLine.ListFilesUtility;
import luaguard.obfuscator.Obfuscator;
import luaguard.obfuscator.ObfuscatorFactory;
import luaguard.unparser.*;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.luaj.vm2.ast.*;
import org.luaj.vm2.parser.*;

/**
 *
 * @author jwulf
 */
public class LuaGuard {
static Logger logger = LogManager.getLogger("GLOBAL"/*LuaGuard.class.getName()*/);
    /**
     * @param argv the command line arguments
     */
    public static void main(String[] argv) {

        //configure logging
        
        logger.setLevel(Level.ALL);
        try {
            Layout layout = new PatternLayout("%-5p [%C]: %m%n");

            FileAppender fileAppender = new FileAppender(layout, "log.log",false);
            logger.addAppender(fileAppender);

            logger.info("Init Logging");

        } catch (IOException ex) {
            System.exit(1);
        }
        logger.info("Start up");
        logger.info("command: " + Arrays.toString(argv));
        
        
        // resource bundle for internationilization
        //Locale local = new Locale("en", "US");
        
        ResourceBundle hints = ResourceBundle.getBundle("luaguard.i18n.CommandLineHints");
        
        // TODO code application logic here
        JCommanderLuaGuard jclg = new JCommanderLuaGuard();
        JCommander mainCommander = new JCommander(jclg,hints/*, argv*/);
        //CommandObfu obfu = new CommandObfu();
        //CommandObfuFolder obfuFold = new CommandObfuFolder();
        //don't need to load since we can do @file for config
        //CommandConfig config = new CommandConfig(); 

        //mainCommander.addCommand("-obfuscate", obfu);
        //mainCommander.addCommand("-obfuscateFolder", obfuFold);
        //don't need to load since we can do @file for config
        //mainCommander.addCommand("-config", config);

        //name of program to print in help
        mainCommander.setProgramName("LuaGuard");
        //mainCommander.setDescriptionsBundle(hints);

        mainCommander.parse(argv);
        /*System.out.println(mainCommander.getParsedCommand());
        System.out.println(mainCommander.getParameters().toString());
        System.out.println(mainCommander.getObjects());
        */
        
        

        
        if (jclg.getHelp()) {
            mainCommander.usage();
        } else if (jclg.getAbout()) {
            System.out.println(hints.getString("aboutText"));
        } else if (true) {
//            ListFilesUtility files = new ListFilesUtility();
//            if (files.exists("c://test2")) {
//                files.listFiles("C://test2");
//            } else {
//                System.out.println("Hey, we didn't find the directory");
//            }

            //change log level from commandline
            int loglevel = 0;
            if (true/*mainCommander.getParameters()*/) {
                loglevel = jclg.getLog();
            }

            switch (loglevel) {
                case 0:
                    logger.setLevel(Level.OFF);
                    break;
                case 1:
                    logger.setLevel(Level.ALL);
                    break;
                case 2:
                    logger.setLevel(Level.DEBUG);
                    break;
                case 3:
                    logger.setLevel(Level.INFO);
                    break;
                case 4:
                    logger.setLevel(Level.WARN);
                    break;
                case 5:
                    logger.setLevel(Level.ERROR);
                    break;
                case 6:
                    logger.setLevel(Level.FATAL);
                    break;
            }
            logger.info("Logger Level changed to: " + logger.getLevel());

            /*
             * lua parser
             */
            List<String> files = jclg.getfiles();
            
            //System.out.println("file count: " + files.size());
            List<String> outputs = jclg.getOutput();
            PrintStream out;

            //file utility
            ListFilesUtility fileUtil = new ListFilesUtility();
            
            for (int i = 0; i < files.size(); i++) {
                String file = files.get(i);
                try {
                    if (!outputs.isEmpty()) {
                        String output = outputs.get(0);
                        if (fileUtil.exists(output) && !jclg.getForce()) {
                            logger.debug("Output File: " + output + " already exists");
                            System.out.println("output file already exist");
                            System.out.println("would you like to overwrite:[y/n]");
                            Scanner scan = new Scanner(System.in);

                            String token = scan.next();
                            while (!token.equalsIgnoreCase("n")
                                    && !token.equalsIgnoreCase("y")
                                    && !token.equalsIgnoreCase("yes")
                                    && !token.equalsIgnoreCase("no")) {
                                System.out.println("output file already exist");
                                System.out.println("would you like to overwrite:[y/n]");
                                token = scan.next();
                            }
                            if (token.equalsIgnoreCase("n")
                                    && token.equalsIgnoreCase("no")) {
                                logger.debug("Output not over written");
                                System.exit(0);
                            } else {
                                logger.debug("Output overwriten");
                            }
                        }
                        out = new PrintStream(output);
                        outputs.remove(0);
                    } else {
                        logger.info("output is empty");
                        out = System.out;
                    }
                    //check file

                    if (!fileUtil.exists(file)) {
                        System.out.println("input file " + file + " does not exist");
                        logger.fatal("input file " + file + " does not exist");
                        System.exit(1);
                    }

                    LuaParser parser = new LuaParser(new FileInputStream(file));
                    Chunk chunk = parser.Chunk(); // this parses the file

                    //Visitor visit = new LuaUnparser(System.out);
                    /* this is were we would put the obfuscators.
                     * this will probably be a loop structure of some type.
                     */
                    ObfuscatorFactory obFactory = new ObfuscatorFactory();

                    Obfuscator ob = obFactory.constructObfuscator("fpo");

                    chunk.accept(ob);
                    //if(obfu.getForce()){
                    chunk.accept(new LuaUnparser(out, false)); //this unparses the file
                    //} 

                } catch (FileNotFoundException | ParseException | SecurityException ex) {
                    logger.fatal(ex);
                }
            }
            
        }
        
    }
    
}
