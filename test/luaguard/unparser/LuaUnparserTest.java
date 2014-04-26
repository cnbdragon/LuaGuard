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
package luaguard.unparser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Test;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

/**
 * Performs two types of identity tests for unparsing: functionality and source code.
 * Source code: Before and after parsing a file for the second time the source should be identical.
 * Functionality: Before and after parsing a file the code output should be unchanged.
 * 
 * @author jgs
 */
public class LuaUnparserTest {
    
    @Test
    public void SourceCodeIdentityTest() throws FileNotFoundException, ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        InputStream is;         // output from parser runs
        LuaParser parser;
        Chunk chunk;            // output from parser
        LuaUnparser unparser;
        String before, after;   // before after 2nd run of parser
        
        // 1st run
        parser = new LuaParser(new FileInputStream(null));
        chunk = parser.Chunk();
        chunk.accept(new LuaUnparser(ps));
        
        // 2nd run
        is = new ByteArrayInputStream(baos.toByteArray());
        
        before = baos.toString();
        
        parser = new LuaParser(is);
        chunk = parser.Chunk();
        baos.reset();
        chunk.accept(new LuaUnparser(ps));
        
        after = baos.toString();
        
        assert(before.equals(after));
    }
}
