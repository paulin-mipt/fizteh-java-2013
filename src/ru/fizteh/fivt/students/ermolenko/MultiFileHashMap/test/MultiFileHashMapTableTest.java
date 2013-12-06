package ru.fizteh.fivt.students.ermolenko.multifilehashmap.test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapTable;

import java.io.File;

public class MultiFileHashMapTableTest {

    private static MultiFileHashMapTable table;

    @Before
    public void setUp() throws Exception {
        File file = new File("testingTable");
        file.mkdir();
        table = new MultiFileHashMapTable(file);
    }

    @Test
    public void testGetName() throws Exception {

        Assert.assertEquals("testingTable", table.getName());
    }

    @Test
    public void testGetInEnglish() throws Exception {

        table.put("getEnglishKey", "getEnglishValue");
        Assert.assertEquals("getEnglishValue", table.get("getEnglishKey"));
    }

    @Test
    public void testGetInRussian() throws Exception {

        table.put("ключ", "значение");
        Assert.assertEquals("значение", table.get("ключ"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {

        table.get(null);
    }

    @Test
    public void testPutNew() throws Exception {

        Assert.assertNull(table.put("putNewKey", "putNewValue"));
        table.remove("putNewKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {

        table.put(null, "valueOfNullKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws Exception {

        table.put("", "valueOfEmptyKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {

        table.remove(null);
    }

    @Test
    public void testRemoveExisted() throws Exception {

        table.put("Key", "Value");
        Assert.assertEquals("Value", table.remove("Key"));
    }

    @Test
    public void testRemoveNotExisted() throws Exception {

        Assert.assertNull(table.remove("Key"));
    }

    @Test
    public void testSize() throws Exception {
        table.put("Key1", "Value1");
        table.put("Key2", "Value2");
        table.put("Key3", "Value3");
        Assert.assertEquals(3, table.size());
    }

    @Test
    public void testPutAndRollback() throws Exception {
        table.put("Key1", "Value1");
        table.put("Key2", "Value2");
        table.put("Key3", "Value3");
        Assert.assertEquals(3, table.rollback());
    }

    @Test
    public void testPutCommit() throws Exception {
        table.put("Key1", "Value1");
        table.put("Key2", "Value2");
        table.put("Key3", "Value3");
        Assert.assertEquals(3, table.commit());
    }

    @Test
    public void testRemovePutCommit() throws Exception {
        table.put("Key", "Value");
        table.commit();
        table.remove("Key");
        table.put("Key", "Value");
        Assert.assertEquals(0, table.commit());
    }
}
