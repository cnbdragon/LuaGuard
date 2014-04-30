/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        Obfuscator obfFac = factory.constructObfuscator("Does not exist");
        Assert.assertNull("Non-null obfuscator", obfFac);
    }
    
    @Test
    public void FunctionParameterObfuscatorTest() {
        Obfuscator obfFac = factory.constructObfuscator("fpo");
        Assert.assertTrue("Wrong obfuscator constructed", 
                FunctionParameterObfuscator.class.equals(obfFac.getClass()));
    }
}
