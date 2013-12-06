package ru.fizteh.fivt.students.chernigovsky.junit;

import org.junit.*;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.MultiFileHashMapUtils;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableTest {
    private ExtendedMultiFileHashMapTable table;
    private ExtendedMultiFileHashMapTableProvider tableProvider;
    File dbDirectory = new File("./myCoolDatabase");

    @Before
    public void setUp() {
        dbDirectory.mkdir();
        try {
            MultiFileHashMapUtils.delete(dbDirectory);
        } catch (IOException ex) {
            System.err.println("delete error");
            System.exit(1);
        }
        dbDirectory.mkdir();

        tableProvider = new MultiFileHashMapTableProvider(dbDirectory, false);
        table = tableProvider.createTable("testTable");
    }

    @After
    public void tearDown() {
        tableProvider.removeTable("testTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlValueShouldFail() {
        table.put("key1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlKeyShouldFail() {
        table.put(null, "val1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNlShouldFail() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlShouldFail() {
        table.remove(null);
    }

    @Test
    public void commitDiffCountTest() {
        table.put("key1", "val1");
        table.commit();
        table.put("key1", "val2");
        table.put("key1", "val1");

        Assert.assertEquals("there is not diff", 0, table.commit());
    }

    @Test
    public void putRemoveTest() {
        table.put("key1", "val1");
        table.commit();
        table.remove("key1");
        table.put("key1", "val1");

        Assert.assertEquals("there is not diff", 0, table.commit());
    }

    @Test
    public void rollbackTest() {
        table.put("key1", "val1");
        table.put("key2", "val2");
        table.commit();
        table.put("key3", "val3");
        table.rollback();

        Assert.assertNull("rollback didn't reverse putting key3", table.get("key3"));
    }

    @Test
    public void getRemovedTest() {
        table.put("key1", "val1");
        table.remove("key1");

        Assert.assertNull("key1 wasn't removed", table.get("key1"));
    }

    @Test
    public void getTest() {
        table.put("key1", "val1");
        table.put("key2", "val2");
        table.remove("key1");

        Assert.assertNotNull("key2 wasn't found", table.get("key2"));
    }

    @Test
    public void putOverwriteTest() {
        table.put("key1", "val1");
        table.put("key1", "val2");

        Assert.assertEquals("value wasn't overwritten", "val2", table.get("key1"));
    }

    @Test
    public void putTest() {
        table.put("key1", "val1");
        table.put("key2", "val2");

        Assert.assertEquals("value wasn't stored", "val2", table.get("key2"));
    }

    @Test
    public void removeTest() {
        table.put("key1", "val1");
        table.put("key2", "val2");
        table.put("key1", "val3");
        table.put("key2", "val4");
        table.remove("key2");

        Assert.assertNull("value wasn't removed", table.get("key2"));
    }

    @Test
    public void nameIsCorrect() {
        Assert.assertEquals("Incorrect table name", "testTable", table.getName());
    }

    @Test
    public void sizeIsCorrect() {
        table.put("key1", "val1");
        table.put("key2", "val2");
        table.put("key3", "val3");
        table.put("key4", "val4");
        table.put("key5", "val5");
        Assert.assertEquals("Incorrect size", 5, table.size());
    }



}
