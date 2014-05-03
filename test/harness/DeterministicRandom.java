/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package harness;

import java.util.Random;

/**
 *
 * @author jgs
 */
public class DeterministicRandom extends Random{
    
    public boolean nextBoolean() {
        return true;
    }
    
}
