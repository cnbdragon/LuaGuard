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
package harness;

import harness.exception.ProgramCrashException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import luaguard.obfuscator.Obfuscator;
import luaguard.unparser.LuaUnparser;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

/**
 *
 * @author jgs
 */
public class BehaviourHarness {
    
    public enum LuaVersion {Lua5_1, Lua5_2};
    
    private static class TestProperties {
        
        private Properties prop;
        
        public TestProperties() {
            prop = new Properties();
            
            String propFile = "lua.properties";
            
            try {
                prop.load(new FileInputStream(propFile));
            }
            catch (FileNotFoundException e) {
                System.err.println("File not found: " + propFile);
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public String luaPath(LuaVersion lv) {
            String version = "Lua5_1";
            switch(lv) {
                case Lua5_2:
                    version = "Lua5_2";
                    break;
                default:
            }
            
            return prop.getProperty(version);
        }
        
    }
    
    private static final TestProperties tp = new TestProperties();
    
    /**
     * Compares the output behaviour of a file before and after obfuscation.
     * Assumes Lua 5.1
     * 
     * @param path path to file to test
     * @param obf obfuscator, null if no obfuscation is to be performed
     * @return true if output behaviour is the same, false otherwise
     * @throws IOException
     * @throws ParseException 
     * @throws java.lang.InterruptedException 
     * @throws harness.exception.ProgramCrashException 
     */
    public static boolean isSameOutput(String path, Obfuscator obf) throws IOException, ParseException, InterruptedException, ProgramCrashException {
        List<Obfuscator> obfList = new ArrayList<Obfuscator>();
        obfList.add(obf);
        return isSameOutput(path, obfList, LuaVersion.Lua5_1);
    }

    /**
     * Compares the output behaviour of a file before and after obfuscation.
     * 
     * @param path path to file to test
     * @param obfList List of obfuscators
     * @param lv Enum for the version of Lua that should be run
     * @return true if output behaviour is the same, false otherwise
     * @throws IOException
     * @throws ParseException 
     * @throws java.lang.InterruptedException 
     * @throws harness.exception.ProgramCrashException 
     */
    public static boolean isSameOutput(String path, List<Obfuscator> obfList, LuaVersion lv) throws IOException, ParseException, InterruptedException, ProgramCrashException {
        
        // I/O for reading output & parsing from a string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        InputStream is;
        InputStreamReader isr;
        BufferedReader br;
        
        String before, after;   // program output before and after applying the obfuscation
        
        // Retrieve output behaviour of the original program (before)
        Process process = new ProcessBuilder(tp.luaPath(lv), path).start();
        is = process.getInputStream();
        
        // Program crash = test failure
        if (process.waitFor() != 0) throw new ProgramCrashException();
        
        before = "";
        int c;
        while ((c = is.read()) != -1){
            before += (char)c;
        }
        
        // Retrieve output behaviour of the obfuscated program (after)
        LuaParser parser = new LuaParser(new FileInputStream(path));
        Chunk chunk = parser.Chunk();
        for (Obfuscator obf : obfList) {
            if (null != obf)
                chunk.accept(obf);
        }
        chunk.accept(new LuaUnparser(ps));
        
        String prog = baos.toString();
        process = new ProcessBuilder(tp.luaPath(lv), "-e", prog).start();
        is = process.getInputStream();
        
        // Program crash = test failure
        if (process.waitFor() != 0) return false;

        after = "";
        while ((c = is.read()) != -1){
            after += (char)c;
        }
        
        // Compare behaviour
        return before.equals(after);
    }
}
