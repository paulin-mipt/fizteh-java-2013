package ru.fizteh.fivt.students.vlmazlov.strings.tests;

import org.junit.*;
import ru.fizteh.fivt.students.vlmazlov.strings.StringTable;
import ru.fizteh.fivt.students.vlmazlov.strings.StringTableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

import java.io.File;

public class StringTableTest {
    private StringTable fileMap;
    private StringTableProvider provider;
    private final String root = "StringTableTest";

    @Before
    public void setUp() {
        try {
            File tempDir = FileUtils.createTempDir(root, null);
            provider = new StringTableProvider(tempDir.getPath(), false);
            fileMap = provider.createTable("testTable");
        } catch (ValidityCheckFailedException ex) {
            Assert.fail("validity check failed: " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        provider.removeTable("testTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void puttingNullValueShouldFail() {
        fileMap.put("key1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void puttingNullKeyShouldFail() {
        fileMap.put(null, "val1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingNullShouldFail() {
        fileMap.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removingNullShouldFail() {
        fileMap.remove(null);
    }

    @Test
    public void commitDiffCountTest() {
        fileMap.put("key1", "val1");
        fileMap.put("key1", "val2");
        fileMap.remove("key1");
        fileMap.put("key1", "val3");

        Assert.assertEquals("there is only one diff", 1, fileMap.commit());
    }

    @Test
    public void rollbackTest() {
        fileMap.put("key1", "val1");
        fileMap.put("key2", "val2");
        fileMap.commit();
        fileMap.put("key3", "val3");
        fileMap.rollback();

        Assert.assertNull("rollback didn't reverse putting key5", fileMap.get("key3"));
    }

    @Test
    public void getRemovedTest() {
        fileMap.put("key1", "val1");
        fileMap.remove("key1");

        Assert.assertNull("key1 wasn't removed", fileMap.get("key1"));
    }

    @Test
    public void getValidTest() {
        fileMap.put("key1", "val1");
        fileMap.put("key2", "val2");
        fileMap.remove("key1");

        Assert.assertNotNull("key2 wasn't found", fileMap.get("key2"));
    }

    @Test
    public void putOverwriteTest() {
        fileMap.put("key1", "val1");
        fileMap.put("key1", "val2");

        Assert.assertEquals("value wasn't overwritten", "val2", fileMap.get("key1"));
    }

    @Test
    public void putAddTest() {
        fileMap.put("key1", "val1");
        fileMap.put("key2", "val2");

        Assert.assertEquals("value wasn't stored", "val2", fileMap.get("key2"));
    }

    @Test
    public void removeTest() {
        fileMap.put("key1", "val1");
        fileMap.put("key2", "val2");
        fileMap.put("key1", "val3");
        fileMap.put("key2", "val4");
        fileMap.remove("key2");

        Assert.assertNull("value wasn't removed", fileMap.get("key2"));
    }

    @Test
    public void nameIsCorrect() {
        Assert.assertEquals("Incorrect table name", "testTable", fileMap.getName());
    }

    @Test
    public void sizeIsCorrect() {
        fileMap.put("key1", "val1");
        fileMap.put("key2", "val2");
        fileMap.put("key3", "val3");
        fileMap.put("key4", "val4");
        Assert.assertEquals("Incorrect size", 4, fileMap.size());
    }
} 
