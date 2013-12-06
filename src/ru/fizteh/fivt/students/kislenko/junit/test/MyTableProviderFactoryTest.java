package ru.fizteh.fivt.students.kislenko.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.junit.MyTableProviderFactory;

import java.io.File;

public class MyTableProviderFactoryTest {
    private MyTableProviderFactory factory = new MyTableProviderFactory();
    private File db;

    @Before
    public void setUp() throws Exception {
        db = new File("db").getCanonicalFile();
        Cleaner.clean(db);
        db.mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNotExistingDatabase() throws Exception {
        factory.create("BadBadDatabase");
    }

    @Test
    public void testCreateNormalDatabase() throws Exception {
        factory.create("db");
    }

    @After
    public void tearDown() throws Exception {
        Cleaner.clean(db);
    }
}
