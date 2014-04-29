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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.luaj.vm2.parser.ParseException;

import harness.BehaviourHarness;
import harness.TransformationHarness;
import org.junit.Test;
import junit.framework.Assert;

/**
 * Performs two types of identity tests for unparsing: functionality and source code.
 * Source code: Before and after parsing a file for the second time the source should be identical.
 * Functionality: Before and after parsing a file the code output should be unchanged.
 * 
 * @author jgs
 */
public class LuaUnparserTest {
    
    @Test
    public void sourceCodeIdentityTest() throws FileNotFoundException, ParseException, IOException {
        String path = "../test/classes.lua";
        Assert.assertTrue("Different behaviour", 
                TransformationHarness.isSameSourceCode(path, null));
        Assert.assertTrue("Not identity transformation", 
                BehaviourHarness.isSameOutput(path, null));
    }

}
