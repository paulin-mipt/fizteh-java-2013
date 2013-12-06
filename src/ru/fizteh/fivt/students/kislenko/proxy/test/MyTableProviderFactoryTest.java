package ru.fizteh.fivt.students.kislenko.proxy.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.junit.test.Cleaner;
import ru.fizteh.fivt.students.kislenko.proxy.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactoryTest {
    private static MyTableProviderFactory factory = new MyTableProviderFactory();
    private static File db;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        db = new File("db").getCanonicalFile();
        Cleaner.clean(db);
        db.mkdir();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Cleaner.clean(db);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }

    @Test(expected = IOException.class)
    public void testCreateNotExistingDatabase() throws Exception {
        factory.create("BadBadDatabase");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateDotsDatabase() throws Exception {
        factory.create("..");
    }

    @Test
    public void testCreateNormalDatabase() throws Exception {
        factory.create("db");
    }

    @Test(expected = IllegalStateException.class)
    public void testClosedFactory() throws Exception {
        MyTableProviderFactory closedFactory = new MyTableProviderFactory();
        closedFactory.close();
        closedFactory.create("table");
    }
}
