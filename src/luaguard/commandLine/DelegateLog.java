/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.commandLine;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 *
 * @author jwulf
 */

@Parameters(
        resourceBundle = "luaguard.i18n.CommandLineHints")

public class DelegateLog {
    @Parameter(names = {"-l", "-log"},
            validateWith = LogLevelValidator.class,
            descriptionKey = "log")
    private Integer verbose = 6;
    
    /**
     *
     * @return
     */
    public int getL() {
        return verbose;
    }
}
