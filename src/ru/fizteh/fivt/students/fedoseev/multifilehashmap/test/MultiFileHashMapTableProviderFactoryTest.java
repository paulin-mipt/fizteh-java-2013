package ru.fizteh.fivt.students.fedoseev.multifilehashmap.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTableProvider;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderFactoryTest {
    private MultiFileHashMapTableProviderFactory tpf;
    private File db;

    public MultiFileHashMapTableProviderFactoryTest() throws IOException {
        tpf = new MultiFileHashMapTableProviderFactory();
        db = new File("database").getCanonicalFile();

        db.mkdirs();
    }

    @Test
    public void testCreate() throws Exception {
        MultiFileHashMapTableProvider tp = tpf.create("database");
        Assert.assertNotNull(tp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        tpf.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmpty() throws Exception {
        tpf.create("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNotExistingDatabase() throws Exception {
        tpf.create("despair");
    }

    @After
    public void tearDown() {
        db.delete();
    }
}
