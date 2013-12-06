package ru.fizteh.fivt.students.inaumov.multifilemap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.base.DatabaseFactory;

public class DatabaseTableProviderTest {
    TableProviderFactory factory;
    TableProvider provider;

    @Before
    public void prepare() {
        factory = new DatabaseFactory();
        provider = factory.create("/database_test");
        provider.createTable("table1");
        provider.createTable("table2");
    }

    @Test
    public void testGetTable() throws Exception {
        Assert.assertNull(provider.getTable("xxxtable1"));
        Assert.assertNull(provider.getTable("xxxtable2"));

        Assert.assertNotNull(provider.getTable("table1"));
        Assert.assertNotNull(provider.getTable("table2"));

        Table table1 = provider.getTable("table1");
        Assert.assertSame(table1, provider.getTable("table1"));
    }

    @Test
    public void testCreateTable() throws Exception {
        Assert.assertNotNull(provider.createTable("newtable1"));
        Assert.assertNotNull(provider.createTable("newtable2"));

        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNull(provider.createTable("table2"));

        provider.removeTable("newtable1");
        provider.removeTable("newtable2");
    }

    @Test
    public void testRemoveTable() throws Exception {
        provider.createTable("newtable1");
        provider.createTable("newtable2");

        provider.removeTable("newtable1");
        provider.removeTable("newtable2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableException() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableExceptionArgument() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableExceptionState() {
        provider.removeTable("xxxtable1");
        provider.removeTable("xxxtable2");
    }
}
