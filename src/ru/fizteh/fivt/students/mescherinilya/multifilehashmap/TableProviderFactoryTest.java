package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TableProviderFactoryTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        try {
            existingDir = folder.newFolder("existingDirPath");
            existingFile = folder.newFile("existingPath");
        } catch (IOException e) {
            System.err.println("can't make tests");
        }
        notExistingFile = new File(folder + File.separator + "notExistingPath");

    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNull() {
        TableProviderFactory factory = new TableProviderFactory();
        factory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateEmpty() {
        TableProviderFactory factory = new TableProviderFactory();
        factory.create("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateBadName() {
        TableProviderFactory factory = new TableProviderFactory();
        factory.create("        ");
    }


    @Test
    public void testCreate() throws Exception {
        TableProviderFactory factory = new TableProviderFactory();
        try {
            factory.create(existingDir.getParent());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }

        try {
            factory.create(notExistingFile.getAbsolutePath());
            assertTrue(notExistingFile.exists() && notExistingFile.isDirectory());
        } catch (IllegalArgumentException e1) {
            //ok
        } catch (Exception e2) {
            fail("initialize notExistingDir: expected IllegalArgumentException");
        }

        if (existingFile.isFile()) {
            try {
                factory.create(existingFile.getAbsolutePath());
                fail("initialize TableProvider with file: expected IllegalArgumentException");
            } catch (IllegalArgumentException e1) {
                //ok
            } catch (Exception e2) {
                fail("initialize TableProvider with file: expected IllegalArgumentException");
            }
        }
    }

}
