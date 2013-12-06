package ru.fizteh.fivt.students.fedoseev.storeable.test;

import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class StoreableTableProviderFactoryTest {
    private static StoreableTableProviderFactory tpf;
    private static File db;

    public StoreableTableProviderFactoryTest() throws IOException {
        db = new File("storehouse").getCanonicalFile();

        db.delete();
        db.mkdirs();

        tpf = new StoreableTableProviderFactory();
    }

    @After
    public void tearDown() {
        db.delete();
    }

    @Test
    public void testCreate() throws Exception {
        tpf.create("storehouse");
    }

    @Test(expected = IOException.class)
    public void testCreateNotExistingDb() throws Exception {
        tpf.create("WTF");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateIllegalSymbolDb() throws Exception {
        tpf.create(".oops.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullDb() throws Exception {
        tpf.create(null);
    }
}
