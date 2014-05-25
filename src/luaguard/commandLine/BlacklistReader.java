/*
 * Copyright 2014 jwulf.
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


package luaguard.commandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jwulf
 */
public class BlacklistReader {

    public List<String> readFile(String directoryName) {
        File file = new File(directoryName);
        Scanner input = null;
        
        List<String> temp = new ArrayList();
        try {
            input = new Scanner(file);
            //input.useDelimiter("\\s+");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BlacklistReader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(3);
        }

        while (input.hasNext()) {
            String nextToken = input.next();
            //or to process line by line
            //String nextLine = input.nextLine();
            //System.out.println(nextToken);
            temp.add(nextToken);
        }

        input.close();
        return temp;
    }

}
