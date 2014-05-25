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
import harness.BehaviourHarness.LuaVersion;
import harness.DeterministicRandom;
import harness.TransformationHarness;
import harness.exception.ProgramCrashException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.luaj.vm2.parser.ParseException;

/**
 *
 * @author Joshua
 */
public class MultipleObfuscatorsTest {
    
    private List<Obfuscator> obfList;
    private List<String> obfNames;
    
    @Before
    public void setup() {
        obfList = new ArrayList<Obfuscator>();
        ObfuscatorFactory obfFactory = new ObfuscatorFactory();
        obfNames = ObfuscatorFactory.getObfuscatorList();
        List<String> blacklist = new ArrayList<String>();
        Random rnd = new DeterministicRandom();
        for (String name : obfNames) {
            obfList.add(obfFactory.constructObfuscator(name, blacklist, rnd));
        }
    }
    
    /**
     * Performs pairwise behaviour check over all obfuscators. Defaults to Lua 5.1
     * @param path path to file to test
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     * @throws ProgramCrashException 
     */
    public void pairwiseBehaviourCheck(String path) throws IOException, ParseException, InterruptedException, ProgramCrashException {
        pairwiseBehaviourCheck(path, LuaVersion.Lua5_1);
    }

    /**
     * Performs pairwise behaviour check over all obfuscators.
     * @param path path to file to test
     * @param lv Version of Lua to use
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     * @throws ProgramCrashException
     */
    public void pairwiseBehaviourCheck(String path, LuaVersion lv) throws IOException, ParseException, InterruptedException, ProgramCrashException {
        List<Obfuscator> obfPair = new ArrayList<Obfuscator>();
        obfPair.add(null);
        obfPair.add(null);
        for (int i = 0; i < obfList.size() - 1; i++) {
            obfPair.set(0, obfList.get(i));
            for (int j = i+1; j < obfList.size(); j++) {
                obfPair.set(1, obfList.get(j));
                Assert.assertTrue("Different behaviour: " + obfNames.get(i) + " -> " + obfNames.get(j),
                        BehaviourHarness.isSameOutput(path, obfPair, lv));
                Collections.swap(obfPair, 0, 1);
                Assert.assertTrue("Different behaviour: " + obfNames.get(i) + " <- " + obfNames.get(j),
                        BehaviourHarness.isSameOutput(path, obfPair, lv));
            }
        }
    }
    
    /**
     * Performs pairwise source code check over all obfuscators.
     * @param path path to file to test
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    public void pairwiseSourceCodeCheck(String path) throws FileNotFoundException, ParseException {
        List<Obfuscator> obfPair = new ArrayList<Obfuscator>();
        obfPair.add(null);
        obfPair.add(null);
        for (int i = 0; i < obfList.size() - 1; i++) {
            obfPair.set(0, obfList.get(i));
            for (int j = i+1; j < obfList.size(); j++) {
                obfPair.set(1, obfList.get(j));
                Assert.assertFalse("Identity transformation: " + obfNames.get(i) + " -> " + obfNames.get(j),
                        TransformationHarness.isSameSourceCode(path, obfPair));
                Collections.swap(obfPair, 0, 1);
                Assert.assertFalse("Identity transformation: " + obfNames.get(i) + " <- " + obfNames.get(j),
                        TransformationHarness.isSameSourceCode(path, obfPair));
            }
        }
    }
    
    @Test
    public void classesBehaviourTest() throws FileNotFoundException, ParseException, IOException, InterruptedException, ProgramCrashException {
        pairwiseBehaviourCheck("Lua/classes.lua");
    }

    @Test
    public void classesSourceCodeTest() throws FileNotFoundException, ParseException, IOException {
        pairwiseSourceCodeCheck("Lua/classes.lua");
    }
    
    @Test
    public void classes2BehaviourTest() throws FileNotFoundException, ParseException, IOException, InterruptedException, ProgramCrashException {
        pairwiseBehaviourCheck("Lua/classes2.lua");
    }

    @Test
    public void classes2SourceCodeTest() throws FileNotFoundException, ParseException, IOException {
        pairwiseSourceCodeCheck("Lua/classes2.lua");
    }
    
    @Test
    public void factorsBehaviourTest() throws FileNotFoundException, ParseException, IOException, InterruptedException, ProgramCrashException {
        pairwiseBehaviourCheck("Lua/factors.lua");
    }

    @Test
    public void factorsSourceCodeTest() throws FileNotFoundException, ParseException, IOException {
        pairwiseSourceCodeCheck("Lua/factors.lua");
    }
    
    @Test
    public void functionsBehaviourTest() throws FileNotFoundException, ParseException, IOException, InterruptedException, ProgramCrashException {
        pairwiseBehaviourCheck("Lua/functions.lua");
    }

    @Test
    public void functionsSourceCodeTest() throws FileNotFoundException, ParseException, IOException {
        pairwiseSourceCodeCheck("Lua/functions.lua");
    }
}