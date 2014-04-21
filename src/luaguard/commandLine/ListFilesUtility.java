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

/**
 *
 * @author jwulf
 */


public class ListFilesUtility {
     /**
     * List all the files and folders from a directory
     * @param directoryName to be listed
     */
    public void listFilesAndFolders(String directoryName){
 
        File directory = new File(directoryName);
 
        //get all the files from a directory
        File[] fList = directory.listFiles();
 
        for (File file : fList){
            System.out.println(file.getName());
        }
    }
 
    /**
     * List all the files under a directory
     * @param directoryName to be listed
     */
    public void listFiles(String directoryName){
 
        File directory = new File(directoryName);
 
        //get all the files from a directory
        File[] fList = directory.listFiles();
 
        for (File file : fList){
            if (file.isFile()){
                System.out.println(file.getName());
            }
        }
    }
 
    /**
     * List all the folder under a directory
     * @param directoryName to be listed
     */
    public void listFolders(String directoryName){
 
        File directory = new File(directoryName);
 
        //get all the files from a directory
        File[] fList = directory.listFiles();
 
        for (File file : fList){
            if (file.isDirectory()){
                System.out.println(file.getName());
            }
        }
    }
 
    /**
     * List all files from a directory and its subdirectories
     * @param directoryName to be listed
     */
    public void listFilesAndFilesSubDirectories(String directoryName){
 
        File directory = new File(directoryName);
 
        //get all the files from a directory
        File[] fList = directory.listFiles();
 
        for (File file : fList){
            if (file.isFile()){
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()){
                listFilesAndFilesSubDirectories(file.getAbsolutePath());
            }
        }
    }
 
    public boolean exists(String directoryName){
        File directory = new File(directoryName);
        return directory.exists();
    }
    public static void main (String[] args){
 
        ListFilesUtility listFilesUtil = new ListFilesUtility();
 
        final String directoryLinuxMac ="/Users/jwulf/test";
 
        //Windows directory example
        final String directoryWindows ="C://test";
 
        listFilesUtil.listFilesAndFolders(directoryWindows);
    }
}
