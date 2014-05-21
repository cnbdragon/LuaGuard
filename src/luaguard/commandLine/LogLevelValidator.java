/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.commandLine;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

/**
 *
 * @author jwulf
 */
public class LogLevelValidator implements IParameterValidator {

    /**
     *
     * @param name
     * @param value
     * @throws ParameterException
     */
    @Override
 public void validate(String name, String value)
      throws ParameterException {
    int n = Integer.parseInt(value);
    if (n < 0 || n > 6) {
      throw new ParameterException("Parameter " + name + 
              " should be a number Level of logging  1:ALL < 2:DEBUG < 3:INFO <"+
              " 4:WARN < 5:ERROR < 6:FATAL < 0:OFF (found " + value +")");
    }
  }
}
