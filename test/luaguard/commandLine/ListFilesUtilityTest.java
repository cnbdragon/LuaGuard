/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.commandLine;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jwulf
 */
public class ListFilesUtilityTest {
    
    public ListFilesUtilityTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of listFilesAndFolders method, of class ListFilesUtility.
     */
    @Test
    public void testListFilesAndFolders() {
        System.out.println("listFilesAndFolders");
        String directoryName = "";
        ListFilesUtility instance = new ListFilesUtility();
        instance.listFilesAndFolders(directoryName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFiles method, of class ListFilesUtility.
     */
    @Test
    public void testListFiles() {
        System.out.println("listFiles");
        String directoryName = "";
        ListFilesUtility instance = new ListFilesUtility();
        instance.listFiles(directoryName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFolders method, of class ListFilesUtility.
     */
    @Test
    public void testListFolders() {
        System.out.println("listFolders");
        String directoryName = "";
        ListFilesUtility instance = new ListFilesUtility();
        instance.listFolders(directoryName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFilesAndFilesSubDirectories method, of class ListFilesUtility.
     */
    @Test
    public void testListFilesAndFilesSubDirectories() {
        System.out.println("listFilesAndFilesSubDirectories");
        String directoryName = "";
        ListFilesUtility instance = new ListFilesUtility();
        instance.listFilesAndFilesSubDirectories(directoryName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exists method, of class ListFilesUtility.
     */
    @Test
    public void testExists() {
        System.out.println("exists");
        String directoryName = "";
        ListFilesUtility instance = new ListFilesUtility();
        boolean expResult = false;
        boolean result = instance.exists(directoryName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class ListFilesUtility.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ListFilesUtility.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sameFile method, of class ListFilesUtility.
     */
    @Test
    public void testSameFile() {
        System.out.println("sameFile");
        List<String> l1 = null;
        List<String> l2 = null;
        ListFilesUtility instance = new ListFilesUtility();
        boolean expResult = false;
        boolean result = instance.sameFile(l1, l2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
