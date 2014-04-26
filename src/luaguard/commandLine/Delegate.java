/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.commandLine;

import com.beust.jcommander.Parameter;

/**
 *
 * @author jwulf
 */
public class Delegate {
    @Parameter(names = {"-l", "-log"},
            validateWith = LogLevelValidator.class,
            description = "Level of logging  1:ALL < 2:DEBUG < 3:INFO < 4:WARN < 5:ERROR < 6:FATAL < 0:OFF")
    private Integer verbose = 6;
    
     public int getL() {
        return verbose;
    }
}
