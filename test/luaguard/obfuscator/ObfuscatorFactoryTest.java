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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jgs
 */
public class ObfuscatorFactoryTest {
    
    private ObfuscatorFactory factory;
    
    @Before
    public void setup() {
        factory = new ObfuscatorFactory();
    }
    
    @Test
    public void NullReturnTest() {
        Obfuscator obf = factory.constructObfuscator("Does not exist");
        Assert.assertNull("Non-null obfuscator", obf);
    }

    @Test
    public void IdentityObfuscatorTest() {
        Obfuscator obf = factory.constructObfuscator("none");
        Assert.assertTrue("Wrong obfuscator constructed", 
                IdentityObfuscator.class.equals(obf.getClass()));
    }
    
    @Test
    public void FunctionParameterObfuscatorTest() {
        Obfuscator obf = factory.constructObfuscator("fpo");
        Assert.assertTrue("Wrong obfuscator constructed", 
                FunctionParameterObfuscator.class.equals(obf.getClass()));
    }
    
    @Test
    public void ReturnValueObfuscatorTest() {
        Obfuscator obf = factory.constructObfuscator("rvo");
        Assert.assertTrue("Wrong obfuscator constructed", 
                ReturnValueObfuscator.class.equals(obf.getClass()));
    }
    
    @Test
    public void FunctionCallObfuscatorTest() {
        Obfuscator obf = factory.constructObfuscator("fco");
        Assert.assertTrue("Wrong obfuscator constructed", 
                FunctionCallObfuscator.class.equals(obf.getClass()));
    }
}
