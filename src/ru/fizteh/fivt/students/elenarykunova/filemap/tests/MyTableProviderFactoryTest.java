package ru.fizteh.fivt.students.elenarykunova.filemap.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

//import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.elenarykunova.filemap.*;

public class MyTableProviderFactoryTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void prepare() {
        try {
            existingDir = folder.newFolder("existingDirPath");
        } catch (IOException e1) {
            System.err.println("can't make tests");
        }
        try {
            existingFile = folder.newFile("existingPath");
        } catch (IOException e1) {
            System.err.println("can't make tests");
        }
        notExistingFile = new File(folder + File.separator + "notExistingPath");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNull() throws IllegalArgumentException, IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        factory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateEmpty() {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        try {
            factory.create("");
        } catch (IOException e) {
            fail("unexpected exception: expected IllegalArgumentException");
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNl() {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        try {
            factory.create("                         ");
        } catch (IOException e) {
            fail("unexpected exception: expected IllegalArgumentException");
        }
    }
    
    @Test
    public void testCreate() {
        MyTableProviderFactory factory = new MyTableProviderFactory();
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
            fail("inizialize notExistingDir: expected IllegalArgumentException");
        }
                
        if (existingFile.isFile()) {
            try {
                factory.create(existingFile.getAbsolutePath());
                fail("inizialize TableProvider with file: expected IllegalArgumentException");
            } catch (IllegalArgumentException e1) {
                //ok
            } catch (Exception e2) {
                fail("inizialize TableProvider with file: expected IllegalArgumentException");
            }
        }
    }
    
    @Test (expected = IllegalStateException.class)
    public void closeCreate() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        factory.close();
        factory.create(existingDir.getAbsolutePath());
    }

    @Test
    public void close() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        factory.close();
        factory.close();
    }
    
    @Test
    public void closeAll() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        MyTableProvider prov1 = (MyTableProvider) factory.create(existingDir.getParent());
        MyTableProvider prov2 = (MyTableProvider) factory.create(existingDir.getParent());
        MyTableProvider prov3 = (MyTableProvider) factory.create(existingDir.getParent());
        factory.close();
        try {
            prov1.getTable("table1");
            fail("expected IllegalStateException in provider.getTable() after closing provider");
        } catch (IllegalStateException e1) {
            // ok;
        }
        try {
            prov2.getTable("table2");
            fail("expected IllegalStateException in provider.getTable() after closing provider");
        } catch (IllegalStateException e1) {
            // ok;
        }
        try {
            prov3.getTable("table3");
            fail("expected IllegalStateException in provider.getTable() after closing provider");
        } catch (IllegalStateException e1) {
            // ok;
        }

    }

}

