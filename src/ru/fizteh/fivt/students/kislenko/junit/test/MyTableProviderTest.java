package ru.fizteh.fivt.students.kislenko.junit.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.multifilemap.MyTable;
import ru.fizteh.fivt.students.kislenko.multifilemap.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.multifilemap.MyTableProviderFactory;

import java.io.File;

public class MyTableProviderTest {
    private static MyTableProvider provider;
    private static File databaseDir;

    @BeforeClass
    public static void setUp() throws Exception {
        databaseDir = new File("testingDatabase").getCanonicalFile();
        Cleaner.clean(databaseDir);
        databaseDir.mkdir();
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(databaseDir.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNamelessTable() throws Exception {
        provider.createTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBadTable() throws Exception {
        provider.createTable("<(^_^)>");
    }

    @Test
    public void testCreateNormalTable() throws Exception {
        Assert.assertNotNull(provider.createTable("table"));
    }

    @Test
    public void testCreateExistingTable() throws Exception {
        provider.createTable("table1");
        Assert.assertNull(provider.createTable("table1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNamelessTable() throws Exception {
        provider.getTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBadTable() throws Exception {
        provider.getTable("<(o_o)>");
    }

    @Test
    public void testGetNotExistingTable() throws Exception {
        Assert.assertNull(provider.getTable("fedor"));
    }

    @Test
    public void testGetExistingTable() throws Exception {
        MyTable table1 = provider.createTable("CYMKUH");
        Assert.assertEquals(table1, provider.getTable("CYMKUH"));
        MyTable table2 = provider.createTable("java");
        Assert.assertEquals(table1, provider.getTable("CYMKUH"));
        Assert.assertEquals(table2, provider.getTable("java"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() throws Exception {
        provider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNamelessTable() throws Exception {
        provider.removeTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveBadTable() throws Exception {
        provider.removeTable("<(-_-)>");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("wtfTable");
    }

    @Test
    public void testRemoveExistingTable() throws Exception {
        provider.createTable("table");
        provider.removeTable("table");
        provider.createTable("table1");
        provider.createTable("table2");
        provider.removeTable("table1");
    }

    @After
    public void tearDown() {
        Cleaner.clean(databaseDir);
    }
}
