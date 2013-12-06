package ru.fizteh.fivt.students.belousova.storable.tests;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.belousova.storable.StorableTableProviderFactory;
import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;

public class StorableTableProviderFactoryTest {
    private TableProviderFactory tableProviderFactory = new StorableTableProviderFactory();

    @After
    public void tearDown() throws Exception {
        File file = new File("javatest");
        if (file.exists()) {
            FileUtils.deleteDirectory(file);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        tableProviderFactory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmpty() throws Exception {
        tableProviderFactory.create("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNl() throws Exception {
        tableProviderFactory.create("    ");
    }

    @Test
    public void testCreateNotExisted() throws Exception {
        Assert.assertNotNull(tableProviderFactory.create("javatest"));
    }
}
