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
import com.beust.jcommander.ParameterException;
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
import luaguard.traversal.FunctionDeclarationVisitor;
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
        logger.info("Command: " + Arrays.toString(argv));
        
        
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

        //parse the commandline
        try {
            mainCommander.parse(argv);
        } catch (ParameterException ex){
            mainCommander.usage();
            System.out.println(ex.getLocalizedMessage());
            logger.warn(ex.getLocalizedMessage());
            System.exit(1);
        }

        //this is where we deal with the arguments from the cli
        if (jclg.getHelp()) {
            mainCommander.usage();
        } else if (jclg.getAbout()) {
            System.out.println(hints.getString("aboutText"));
        } else if (mainCommander.getParameters().size() > 0 /* true*/) { // to be here we have to have a vailid command with a
                           // -file and a -obfuscator
            
            //change log level from commandline
            int loglevel = 0;
            loglevel = jclg.getLog(); //this will always return a value. if it doesn't exit(1)
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
                default:
                    logger.fatal("Bad log level");
                    System.out.println("Bad log level");
                    System.exit(1);
            }
            logger.info("Logger Level changed to: " + logger.getLevel());

            /* first place it makes sense to go into debug mode*/
            boolean debug = jclg.getDebug();
            logger.info("Debug mode: " + debug);
            
            /*
             * lua parser
             */
            /*list of input files*/
            List<String> files = jclg.getfiles();
            /*list of output files*/
            List<String> outputs = jclg.getOutput();            
            /**/
            PrintStream out;

            //file utility
            ListFilesUtility fileUtil = new ListFilesUtility();
            if(fileUtil.sameFile(files,outputs)){
                System.out.println("Files in input and output are the same");
                logger.fatal("Files in input and output are the same");
                System.exit(1);
            }
            
            /*iterate over the input files*/
            for (int i = 0; i < files.size(); i++) {
                String file = files.get(i);
                try {
                    /* check if output is empty and exists.  
                     * probably should move this block to a new function
                     */
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
                        logger.info("Output is empty");
                        out = System.out;
                    }
                    //check file

                    if (!fileUtil.exists(file)) {
                        System.out.println("Input file " + file + " does not exist");
                        logger.fatal("Input file " + file + " does not exist");
                        System.exit(1);
                    }

                    LuaParser parser = new LuaParser(new FileInputStream(file));
                    Chunk chunk = parser.Chunk(); // this parses the file
                    
                    /* now we will do pre processing traversals
                     *
                    */
                    
                    FunctionDeclarationVisitor fdv = new FunctionDeclarationVisitor();
                    chunk.accept(fdv);

                    /* this is were we would put the obfuscators.
                     * this will probably be a loop structure of some type.
                     */
                    ObfuscatorFactory obFactory = new ObfuscatorFactory();
                    
                    List<String> obfus = jclg.getObfuscators();
                    List<String> blacklist = jclg.getBlacklist();
                    
                    
                    /*iterate throught list of obfuscators*/
                    for(int j = 0; j < obfus.size(); j++ ){
                        Obfuscator ob = obFactory.constructObfuscator(obfus.get(j),fdv.funcPar);
                        if(null != ob){
                            chunk.accept(ob);
                            if(debug){
                                //TODO
                                //chunk.accept(new LuaUnparser(new PrintStream("./debug/"+obfus.get(j)), !debug)); //this unparses the file
                            }
                        } else {
                            logger.warn("could not build: " + obfus.get(j));
                        }
                    }
                    
                    chunk.accept(new LuaUnparser(out, !debug)); //this unparses the file
                    //} 

                } catch (FileNotFoundException | ParseException | SecurityException ex) {
                    logger.fatal(ex);
                }
            }
            
        } 
        
    }
    
}