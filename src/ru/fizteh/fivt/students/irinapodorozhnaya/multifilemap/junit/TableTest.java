package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.junit;

import java.io.File;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MyTableProviderFactory;

public class TableTest {

    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private Table testTable;
    private TableProvider provider;

    @Before
    public void setUp() {
        new File(DATA_BASE_DIR).mkdirs();
        provider = new MyTableProviderFactory().create(DATA_BASE_DIR);
        testTable = provider.createTable("table");
    }

    @After
    public void tearDown() throws Exception {
        provider.removeTable("table");
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(testTable.getName(), "table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWithNull() throws Exception {
        testTable.get(null);
    }

    @Test
    public void testGet() throws Exception {
        testTable.put("getKey", "getValue");
        Assert.assertEquals(testTable.get("getKey"), "getValue");
        Assert.assertNull(testTable.get("key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() throws Exception {
        testTable.put(null, "nullKey");
        testTable.put("nullValue", null);
    }

    @Test
    public void testPut() throws Exception {
        Assert.assertNull(testTable.put("put", "putValue"));
        Assert.assertEquals(testTable.put("put", "putNewValue"), "putValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNull() throws Exception {
        testTable.remove(null);
    }

    @Test
    public void testRemove() throws Exception {
        testTable.put("remove", "removeValue");
        Assert.assertNull(testTable.remove("notRemove"));
        Assert.assertEquals(testTable.remove("remove"), "removeValue");
    }

    @Test
    public void testSize() throws Exception {
        testTable.put("size1", "value1");
        Assert.assertEquals(testTable.size(), 1);
        testTable.put("size2", "value2");
        testTable.commit();
        testTable.put("size2", "value3");
        Assert.assertEquals(testTable.size(), 2);
        testTable.remove("size2");
        Assert.assertEquals(testTable.size(), 1);
        testTable.rollback();
        Assert.assertEquals(testTable.size(), 2);
    }

    @Test
    public void testCommit() throws Exception {
        testTable.put("commit1", "1");
        testTable.put("commit2", "2");
        Assert.assertEquals(testTable.commit(), 2);
        testTable.put("commit3", "3");
        testTable.put("commit2", "3");
        testTable.rollback();
        Assert.assertEquals(testTable.get("commit2"), "2");
        Assert.assertNull(testTable.get("commit3"));
    }

    @Test
    public void testRollback() throws Exception {
        testTable.put("rollback1", "1");
        testTable.put("rollback2", "2");
        testTable.commit();
        testTable.put("rollback1", "3");
        testTable.put("rollback3", "3");
        Assert.assertEquals(testTable.rollback(), 2);
        Assert.assertNull(testTable.get("rollback3"));
        Assert.assertEquals(testTable.get("rollback1"), "1");
    }
}
