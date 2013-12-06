package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProvider;

import java.io.File;

public class MultiFileTableTest {
    private static final String DB_PATH = "/home/andrey/database_test";
    private static TableProvider provider;
    private static Table testTable;

    @Before
    public void setUp() throws Exception {
        provider = new MultiFileTableProvider(new File(DB_PATH));
        testTable = provider.createTable("table3");
    }

    @After
    public void tearDown() throws Exception {
        provider.removeTable("table3");
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(testTable.getName(), "table3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWithNull() throws Exception {
        testTable.get(null);
    }

    @Test
    public void testGet() throws Exception {
        testTable.put("key", "value");
        Assert.assertEquals(testTable.get("key"), "value");
        Assert.assertNull(testTable.get("not_existent_key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutWithNull() throws Exception {
        testTable.put(null, "value");
        testTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutWithSpaces() throws Exception {
        testTable.put("   ", "value");
        testTable.put("  ", " ");
        testTable.put("key", " ");
    }

    @Test
    public void testPut() throws Exception {
        Assert.assertNull(testTable.put("new_key", "new_value"));
        Assert.assertEquals(testTable.put("new_key", "new_value_2"), "new_value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNull() throws Exception {
        testTable.remove(null);
    }

    @Test
    public void testRemove() throws Exception {
        testTable.put("1", "2");
        Assert.assertNull(testTable.remove("not_existing_key"));
        Assert.assertNull(testTable.remove("not_existing_key2"));
        Assert.assertEquals(testTable.remove("1"), "2");
    }

    @Test
    public void testSize() throws Exception {
        for (int i = 0; i < 7; ++i) {
            testTable.put("sizeKey" + i, Integer.toString(i));
        }
        Assert.assertEquals(testTable.size(), 7);
        testTable.remove("sizeKey5");
        Assert.assertEquals(testTable.size(), 6);
        testTable.rollback();
        Assert.assertEquals(testTable.size(), 0);
    }

    @Test
    public void testCommit() throws Exception {
        testTable.put("1", "2");
        testTable.put("3", "4");
        testTable.put("5", "6");
        Assert.assertEquals(testTable.commit(), 3);
    }

    @Test
    public void testRollback() throws Exception {
        for (int i = 0; i < 7; ++i) {
            testTable.put("rollbackKey" + i, Integer.toString(i));
        }
        testTable.commit();
        testTable.put("rollbackKey4", "value4");
        testTable.put("newKey", "newValue");
        Assert.assertEquals(testTable.rollback(), 1);
        Assert.assertNull(testTable.get("newKey"));
        Assert.assertEquals(testTable.get("rollbackKey4"), "4");
    }
}
