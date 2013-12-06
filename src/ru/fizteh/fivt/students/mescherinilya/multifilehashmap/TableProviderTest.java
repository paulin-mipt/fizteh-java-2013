package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


import java.io.File;
import java.io.IOException;

public class TableProviderTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() {
        try {
            existingDir = folder.newFolder("existingDirPath");
            existingFile = folder.newFile("existingPath");
        } catch (IOException e1) {
            System.err.println("can't make tests");
        }
        notExistingFile = new File(existingDir.getParent() + File.separator + "notExistingPath");
    }


    @Test
    public void testInitialize() {
        TableProvider provider;
        try {
            provider = new TableProvider(existingDir.getParentFile());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }

        try {
            provider = new TableProvider(notExistingFile);
            assertTrue(notExistingFile.exists() && notExistingFile.isDirectory());
        } catch (IllegalArgumentException e1) {
            //ok
        } catch (Exception e2) {
            fail("initialize notExistingDir: expected IllegalArgumentException");
        }

        if (existingFile.isFile()) {
            try {
                provider = new TableProvider(existingFile);
                fail("initialize TableProvider with file: expected IllegalArgumentException");
            } catch (IllegalArgumentException e1) {
                //ok
            } catch (Exception e2) {
                fail("initialize TableProvider with file: expected IllegalArgumentException");
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() {
        TableProvider provider = new TableProvider(existingDir);
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() {
        TableProvider provider = new TableProvider(existingDir);
        provider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNl() {
        TableProvider provider = new TableProvider(existingDir);
        provider.getTable("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() {
        TableProvider provider = new TableProvider(existingDir);
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmpty() {
        TableProvider provider = new TableProvider(existingDir);
        provider.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNl() {
        TableProvider provider = new TableProvider(existingDir);
        provider.createTable("                  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() {
        TableProvider provider = new TableProvider(existingDir);
        provider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableEmpty() {
        TableProvider provider = new TableProvider(existingDir);
        provider.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNl() {
        TableProvider provider = new TableProvider(existingDir);
        provider.createTable("                   ");
    }

    @Test(expected = RuntimeException.class)
    public void testGetTableBadSymbol() {
        TableProvider provider = new TableProvider(existingDir);
        provider.getTable(".aa.aa.");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableBadSymbol() {
        TableProvider provider = new TableProvider(existingDir);
        provider.createTable("/aaaa");
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveTableBadSymbol() {
        TableProvider provider = new TableProvider(existingDir);
        provider.getTable("\\aa\\aa");
    }

    @Test
    public void testGetTable() {
        TableProvider provider = new TableProvider(existingDir.getParentFile());
        try {
            Table res = provider.getTable(notExistingFile.getName());
            assertNull(res);
        } catch (IllegalArgumentException e1) {
            fail("Unexpected IllegalArgumentException");
        } catch (RuntimeException e) {
            //ok, shit happens, could throw exception
        }

        if (existingFile.isFile()) {
            try {
                Table res2 = provider.getTable("existingPath");
                assertNull(res2);
            } catch (RuntimeException e) {
                //ok, shit happens, fileMap could throw exception
            }
        }
        if (existingDir.isDirectory()) {
            try {
                Table res2 = provider.getTable("existingDirPath");
                assertNotNull(res2);
            } catch (RuntimeException e) {
                //ok, shit happens, fileMap could throw exception
            }
        }
    }

    @Test
    public void testCreateTable() {
        TableProvider provider = null;
        try {
            provider = new TableProvider(existingDir.getParentFile());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }

        try {
            Table res = provider.createTable("notExistingPath");
            assertNotNull(res);
            assertTrue(notExistingFile.exists() && notExistingFile.isDirectory());
        } catch (RuntimeException e1) {
            // what a pity: couldn't create this file
        }

        if (existingDir.isDirectory()) {
            Table res2 = provider.createTable("existingDirPath");
            assertNull(res2);
        }
    }

    @Test
    public void testRemoveTable() {
        TableProvider provider = new TableProvider(existingDir.getParentFile());

        try {
            provider.removeTable(notExistingFile.getName());
            fail("remove notExistingTable: expected IllegalStateException");
        } catch (IllegalStateException e1) {
            //ok
        } catch (Exception e2) {
            fail("remove notExistingTable: expected IllegalStateException");
        }

        if (existingFile.isFile()) {
            try {
                provider.removeTable("existingPath");
                fail("remove not table: expected IllegalStateException");
            } catch (IllegalStateException e1) {
                //ok
            } catch (Exception e2) {
                fail("remove not table: expected IllegalStateException");
            }
        }
        if (existingDir.isDirectory()) {
            try {
                provider.removeTable("existingDirPath");
                assertTrue(!existingDir.exists());
            } catch (RuntimeException e1) {
                //ok, can't delete, shit happens.
            }
        }
    }

    @Test
    public void testCreateGetRemove() {
        TableProvider provider = new TableProvider(existingDir.getParentFile());
        String name = "newTable";
        Table createRes = provider.createTable(name);
        Table getRes = provider.getTable(name);
        if (createRes != getRes) {
            fail("table from get should be the same as from create");
        }
        Table createAgain = provider.createTable(name);
        assertNull(createAgain);
        provider.removeTable(name);
        assertNull(provider.getTable(name));
    }

}
