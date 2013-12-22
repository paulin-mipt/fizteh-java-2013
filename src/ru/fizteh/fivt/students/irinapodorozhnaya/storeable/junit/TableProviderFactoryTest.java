package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.junit;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.MyTableProviderFactory;

public class TableProviderFactoryTest {
    private MyTableProviderFactory factory;
    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private final File curDir = new File(DATA_BASE_DIR);

    @Before
    public void setUp() throws Exception {
        curDir.mkdirs();
        factory = new MyTableProviderFactory();
    }
    
    @After
    public void tearDown() {
        CommandRemove.deleteRecursivly(curDir);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateIllegal() throws Exception {
        factory.create("%*&YIH&?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFile() throws Exception {
        File file = new File(curDir, "file");
        file.createNewFile();
        factory.create(file.getAbsolutePath());
    }
    
    @Test(expected = IOException.class)
    public void testCreateNonExisting() throws Exception {
        File file = new File(curDir, "non-existing-file");
        factory.create(file.getName());
    }

    @Test
    public void testCreateLegal() throws Exception {
        Assert.assertNotNull(factory.create(DATA_BASE_DIR));
    }

    @Test
    public void doubleClose() throws Exception {
        factory.close();
        factory.close();
    }

    @Test (expected = IllegalStateException.class)
    public void createAfterClose() throws Exception {
        factory.close();
        factory.create(DATA_BASE_DIR);
    }
}
