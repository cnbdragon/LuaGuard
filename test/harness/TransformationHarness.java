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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import luaguard.obfuscator.Obfuscator;
import luaguard.unparser.LuaUnparser;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

/**
 *
 * @author jgs
 */
public class TransformationHarness {
    
    /**
     * Compares the source code generated by the combination of the parser and unparser.
     * Unparse(Parse(Unparse(Parse(Code)))) == Unparse(Parse(Code))
     * 
     * @param path path to file to test
     * @param obf obfuscator, null if no obfuscation is to be performed
     * @return true if the code output from 1st run is the same as the output from the 2nd run
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    public static boolean isSameSourceCode(String path, Obfuscator obf) throws FileNotFoundException, ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        InputStream is;         // output from parser runs
        Chunk chunk;            // output from parser
        String before, after;   // before after 2nd run of parser
        
        // 1st run
        LuaParser parser = new LuaParser(new FileInputStream(path));
        chunk = parser.Chunk();
        chunk.accept(new LuaUnparser(ps));
        
        // 2nd run
        is = new ByteArrayInputStream(baos.toByteArray());
        
        before = baos.toString();   // output from 1st run
        
        parser = new LuaParser(is);
        chunk = parser.Chunk();
        if (null != obf)
            chunk.accept(obf);
        baos.reset();
        chunk.accept(new LuaUnparser(ps));
        
        after = baos.toString();    // output from 2nd run
        
        return before.equals(after); 
    }
    
    /**
     * Performs a traversal of the AST of a program with the supplied visitor.
     * 
     * @param path path to file to test
     * @param v visitor
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    public static void setupRun(String path, Visitor v) throws FileNotFoundException, ParseException {
        LuaParser parser = new LuaParser(new FileInputStream(path));
        Chunk chunk = parser.Chunk();
        if (null != v)
            chunk.accept(v);
    }
}
