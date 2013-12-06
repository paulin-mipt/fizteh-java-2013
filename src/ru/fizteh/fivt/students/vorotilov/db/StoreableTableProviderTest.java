package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableProviderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCreateAndRemoveTable() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        Assert.assertNotNull(tableProvider.createTable("newTable", classes));
        Assert.assertNull(tableProvider.createTable("newTable", classes));
        tableProvider.removeTable("newTable");
    }

    @Test
    public void testGetTable() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        StoreableTable table = tableProvider.createTable("newTable", classes);
        Assert.assertNotNull(tableProvider.getTable("newTable"));
        Assert.assertEquals(table, tableProvider.getTable("newTable"));
        tableProvider.removeTable("newTable");
    }

    @Test
    public void testGetNotExistedTable() throws IOException {
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        Assert.assertNull(tableProvider.getTable("notExistedTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithNotValidName() throws IOException {
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNotValidName() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable(null, classes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithEmptyName() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable(" ", classes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNullColumnTypesShoulFail() throws IOException {
        List<Class<?>> classes = null;
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable("tta", classes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithout() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable("tta", classes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithEmptyName() throws IOException {
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.removeTable("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithEmptyName() throws IOException {
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithNotValidName() throws IOException {
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistedTable() throws IOException {
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.removeTable("notExistedTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSymbolsInTableName() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable("//a", classes);
    }

    @Test(expected = ColumnFormatException.class)
    public void testAlienStoreableWithMoreColumns() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        List<Class<?>> wrongClasses = new ArrayList<>();
        wrongClasses.add(String.class);
        wrongClasses.add(Float.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Table testTable = tableProvider.createTable("abcdef", classes);
        tableProvider.serialize(testTable, new TableRow(wrongClasses));
    }

    @Test(expected = ColumnFormatException.class)
    public void testAlienStoreableWithLessColumns() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        classes.add(Float.class);
        List<Class<?>> wrongClasses = new ArrayList<>();
        wrongClasses.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Table testTable = tableProvider.createTable("abcdef", classes);
        tableProvider.serialize(testTable, new TableRow(wrongClasses));
    }

}
