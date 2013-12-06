package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableProvider;

import java.io.File;

public class MultiFileTableProviderTest {
    private static final String DB_PATH = "/home/andrey/database_test";
    TableProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new MultiFileTableProvider(new File(DB_PATH));
    }

    @Test
    public void testCreateTable() throws Exception {
        Assert.assertNotNull(provider.createTable("table1"));
        Assert.assertNull(provider.createTable("table1"));
        provider.removeTable("table1");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableBadSymbols() throws Exception {
        provider.createTable(":123+");
    }

    @Test(expected = RuntimeException.class)
    public void testGetTableBadSymbols() throws Exception {
        provider.getTable(":123+");
    }

    @Test
    public void testGetTableInstance() throws Exception {
        Table table = provider.createTable("newtable");
        Assert.assertSame(provider.getTable("newtable"), table);
        Assert.assertSame(provider.getTable("newtable"), provider.getTable("newtable"));
    }

    @Test
    public void testGetTable() {
        provider.createTable("table2");
        Assert.assertNotNull(provider.getTable("table2"));
        provider.removeTable("table2");
    }

    @Test
    public void testGetNonExistingTable() {
        Assert.assertNull(provider.getTable("unknownTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithNull() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNull() throws Exception {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithNull() throws Exception {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
    }
}
