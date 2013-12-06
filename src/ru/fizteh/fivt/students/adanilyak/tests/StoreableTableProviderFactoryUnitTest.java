package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProviderFactory;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:43
 */
public class StoreableTableProviderFactoryUnitTest {
    StoreableTableProviderFactory testProviderFactory;
    String sandBoxDirectory;
    File sandBoxFile;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUpTestObject() throws IOException {
        sandBoxFile = folder.newFolder();
        sandBoxDirectory = sandBoxFile.getAbsolutePath();
        testProviderFactory = new StoreableTableProviderFactory();
    }

    @After
    public void tearDownTestObject() throws IOException {
        DeleteDirectory.rm(sandBoxFile);
    }

    /**
     * TEST BLOCK
     * CREATE TABLE PROVIDER TESTS
     */

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableProviderTest() {
        try {
            testProviderFactory.create(null);
        } catch (IOException exc) {
            System.err.println("storeable table provider factory unit test: create: something went wrong");
        }
    }

    /**
     * TEST BLOCK
     * CLOSE TESTS
     */

    @Test(expected = IllegalStateException.class)
    public void createAfterClose() throws IOException {
        testProviderFactory.close();
        testProviderFactory.create(sandBoxDirectory);
    }

    @Test
    public void openExistingProviderUsingCreateAfterClose() throws IOException {
        StoreableTableProvider testProvider = (StoreableTableProvider) testProviderFactory.create(sandBoxDirectory);
        testProvider.close();
        StoreableTableProvider testProviderNewReference =
                (StoreableTableProvider) testProviderFactory.create(sandBoxDirectory);
        Assert.assertNotEquals(testProvider, testProviderNewReference);
    }
}
