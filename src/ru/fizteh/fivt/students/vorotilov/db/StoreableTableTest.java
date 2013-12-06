package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testGetName() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        Assert.assertEquals(currentTableName, currentTable.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        currentTable.put(null, tableRow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        currentTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        currentTable.put("  ", tableRow);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAlienStoreableWithMoreColumns() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        List<Class<?>> wrongClasses = new ArrayList<>();
        classes.add(String.class);
        classes.add(Float.class);
        TableRow tableRow = new TableRow(wrongClasses);
        tableRow.setColumnAt(0, "value");
        tableRow.setColumnAt(1, 3.14);
        currentTable.put("newKey", tableRow);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAlienStoreableWithLessColumns() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        List<Class<?>> wrongClasses = new ArrayList<>();
        classes.add(String.class);
        TableRow tableRow = new TableRow(wrongClasses);
        tableRow.setColumnAt(0, "value");
        tableRow.setColumnAt(1, "3.14");
        currentTable.put("newKey", tableRow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutKeyWithWhitespases() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        currentTable.put("aa bb", tableRow);
    }

    @Test(expected = RuntimeException.class)
    public void testPutN1Value() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "\n");
        currentTable.put("key", tableRow);
    }

    @Test
    public void testPutNewKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        Assert.assertNull(currentTable.put("key", tableRow));
    }

    @Test(expected = ColumnFormatException.class)
    public void testPutWrongKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        List<Class<?>> wrongClasses = new ArrayList<>();
        wrongClasses.add(Integer.class);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, 3);
        Assert.assertNull(currentTable.put("key", tableRow));
    }

    @Test
    public void testPutOldKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "valueOld");
        Assert.assertNull(currentTable.put("key", tableRow));
        TableRow newTableRow = new TableRow(classes);
        newTableRow.setColumnAt(0, "value");
        Assert.assertEquals(currentTable.put("key", newTableRow), tableRow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        currentTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        currentTable.remove("");
    }

    @Test
    public void testRemove() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        Assert.assertNull(currentTable.put("key", tableRow));
        Assert.assertNotNull(currentTable.remove("key"));
        Assert.assertNull(currentTable.remove("key"));
    }

    @Test
    public void testGetNotExistedKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        Assert.assertNull(currentTable.get("notExistedKey"));
    }

    @Test
    public void testGetExistedKey() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        Assert.assertNull(currentTable.put("newKey", tableRow));
        Assert.assertEquals(currentTable.get("newKey"), tableRow);
    }

    @Test
    public void testCommit() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        int sizeBefore = currentTable.size();
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        currentTable.put("key", tableRow);
        Assert.assertEquals(currentTable.commit(), 1);
        Assert.assertEquals(currentTable.size(), sizeBefore + 1);
    }

    @Test
    public void testRollback() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        int sizeBefore = currentTable.size();
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        currentTable.put("key", tableRow);
        Assert.assertEquals(currentTable.rollback(), 1);
        Assert.assertEquals(sizeBefore, currentTable.size());
    }

    @Test
    public void testSize() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        int sizeBefore = currentTable.size();
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "value");
        currentTable.put("key1", tableRow);
        currentTable.put("key2", tableRow);
        Assert.assertEquals(currentTable.commit(), 2);
        Assert.assertEquals(sizeBefore + 2, currentTable.size());
    }

    @Test
    public void testRollbackOverwritePutAndRemove() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow1 = new TableRow(classes);
        tableRow1.setColumnAt(0, "value1");
        TableRow tableRow2 = new TableRow(classes);
        tableRow2.setColumnAt(0, "value2");
        TableRow tableRow3 = new TableRow(classes);
        tableRow3.setColumnAt(0, "value3");
        currentTable.put("key1", tableRow1);
        Assert.assertEquals(currentTable.commit(), 1);
        currentTable.put("key1", tableRow2);
        currentTable.put("key2", tableRow3);
        currentTable.remove("key1");
        Assert.assertEquals(2, currentTable.rollback());
    }

    @Test
    public void testCommitRollback() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow = new TableRow(classes);
        tableRow.setColumnAt(0, "1");
        currentTable.put("1", tableRow);
        currentTable.commit();
        int size = currentTable.size();
        currentTable.rollback();
        Assert.assertEquals(size, currentTable.size());
    }

    @Test
    public void testPutOverwrite() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow1 = new TableRow(classes);
        tableRow1.setColumnAt(0, "value1");
        TableRow tableRow2 = new TableRow(classes);
        tableRow2.setColumnAt(0, "value2");
        currentTable.put("key1", tableRow1);
        currentTable.put("key1", tableRow2);
        Assert.assertEquals(1, currentTable.rollback());
    }

    @Test
    public void testSizeAfterRollback() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(String.class);
        StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
        StoreableTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        StoreableTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName, classes);
        TableRow tableRow1 = new TableRow(classes);
        tableRow1.setColumnAt(0, "value1");
        TableRow tableRow2 = new TableRow(classes);
        tableRow2.setColumnAt(0, "value2");
        TableRow tableRow3 = new TableRow(classes);
        tableRow3.setColumnAt(0, "value3");
        currentTable.put("key1", tableRow1);
        currentTable.put("key2", tableRow2);
        currentTable.put("key3", tableRow3);
        Assert.assertEquals(currentTable.commit(), 3);
        Assert.assertEquals(currentTable.size(), 3);
        currentTable.put("key2", tableRow1);
        Assert.assertEquals(currentTable.size(), 3);
        Assert.assertEquals(currentTable.rollback(), 1);
        Assert.assertEquals(currentTable.size(), 3);
    }

}
