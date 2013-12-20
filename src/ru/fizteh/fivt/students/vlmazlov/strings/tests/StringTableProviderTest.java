package ru.fizteh.fivt.students.vlmazlov.strings.tests;

import org.junit.*;
import java.io.File;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.strings.StringTableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

public class StringTableProviderTest {
    private StringTableProvider provider;
    private final String root = "StringTableProviderTest";

    @Before
    public void setUp() {
        try {
            File tempDir = FileUtils.createTempDir(root, null);
            provider = new StringTableProvider(tempDir.getPath(), false);
        } catch (ValidityCheckFailedException ex) {
            Assert.fail("validity check failed: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingNullShouldFail() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingNullShouldFail() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removingNullShouldFail() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removingNonExistingTableShouldFail() {
        provider.removeTable("testNonExist");
    }

    @Test
    public void gettingNonExistingTableShouldFail() {
        Assert.assertNull("should be null", provider.getTable("testNonExist"));
    }

    @Test
    public void gettingCreatedTable() {
        Table created = provider.createTable("testGet");
        Table firstGet = provider.getTable("testGet");
        Table secondGet = provider.getTable("testGet");
        Assert.assertEquals("should be testGet", "testGet", provider.getTable("testGet").getName());
        Assert.assertSame("getting should returns the same table as create", created, firstGet);
        Assert.assertSame("getting the same table twice should return the same", firstGet, secondGet);
        provider.removeTable("testGet");
    }

    @Test
    public void gettingRemovedTable() {
        provider.createTable("testRemove");
        provider.removeTable("testRemove");
        Assert.assertNull("should be null", provider.getTable("testRemove"));
    }
}
