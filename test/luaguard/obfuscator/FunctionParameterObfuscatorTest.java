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
package luaguard.obfuscator;

import harness.BehaviourHarness;
import harness.TransformationHarness;
import harness.exception.ProgramCrashException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.luaj.vm2.parser.ParseException;

/**
 *
 * @author jgs
 */
public class FunctionParameterObfuscatorTest {
    private Obfuscator obf;
    
    @Before
    public void setup() {
        obf = new FunctionParameterObfuscator();
    }
    
    @Test
    public void classesBehaviourTest() throws FileNotFoundException, ParseException, IOException, InterruptedException, ProgramCrashException {
        String path = "Lua/classes.lua";
        Assert.assertTrue("Different behaviour",
               BehaviourHarness.isSameOutput(path, obf));
    }

    @Test
    public void classesSourceCodeTest() throws FileNotFoundException, ParseException, IOException {
        String path = "Lua/classes.lua";
        Assert.assertTrue("Identity transformation", 
               !TransformationHarness.isSameSourceCode(path, obf));
    }
    
    @Test
    public void classes2BehaviourTest() throws FileNotFoundException, ParseException, IOException, InterruptedException, ProgramCrashException {
        String path = "Lua/classes2.lua";
        Assert.assertTrue("Different behaviour",
               BehaviourHarness.isSameOutput(path, obf));
    }

    @Test
    public void classes2SourceCodeTest() throws FileNotFoundException, ParseException, IOException {
        String path = "Lua/classes2.lua";
        Assert.assertTrue("Identity transformation", 
               !TransformationHarness.isSameSourceCode(path, obf));
    }
    
    @Test
    public void factorsBehaviourTest() throws FileNotFoundException, ParseException, IOException, InterruptedException, ProgramCrashException {
        String path = "Lua/factors.lua";
        Assert.assertTrue("Different behaviour",
               BehaviourHarness.isSameOutput(path, obf));
    }

    @Test
    public void factorsSourceCodeTest() throws FileNotFoundException, ParseException, IOException {
        String path = "Lua/factors.lua";
        Assert.assertTrue("Identity transformation", 
               !TransformationHarness.isSameSourceCode(path, obf));
    }
}
