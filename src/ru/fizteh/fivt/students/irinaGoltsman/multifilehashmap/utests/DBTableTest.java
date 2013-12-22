package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.*;

import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBStoreable;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBTableTest {
    private static Table table;
    private static TableProvider tableProvider;
    private List<Class<?>> columnTypes = new ArrayList<>();
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTable() throws IOException, ParseException {
        columnTypes.add(Integer.class);
        tableProvider = new DBTableProvider(rootDBDirectory.newFolder());
        table = tableProvider.createTable("testTable", columnTypes);
    }

    @Test
    public void sizeIsZeroForNew() {
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void commitIsZeroForNew() throws IOException {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void getName() {
        Assert.assertEquals("testTable", table.getName());
    }

    @Test
    public void getNotExistingKey() {
        Assert.assertNull(table.get("notExistingKey"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKey() {
        table.get(null);
    }

    @Test
    public void putNewKey() {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        Assert.assertNull(table.put("newTestKey", row));
        table.remove("newTestKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        table.put(null, row);
    }

    @Test
    public void removeNotExistingKey() {
        Assert.assertNull(table.remove("notExistingKeyTest"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        table.remove(null);
    }

    @Test
    public void sizeWork() {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        Assert.assertEquals(0, table.size());
        table.put("1", row);
        table.put("2", row);
        table.put("3", row);
        Assert.assertEquals(3, table.size());
        table.remove("1");
        Assert.assertEquals(2, table.size());
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void sizeHardWork() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        Assert.assertEquals(0, table.size());
        table.put("1", row);
        table.commit();
        Assert.assertEquals(1, table.size());
        table.put("2", row);
        table.put("3", row);
        Assert.assertEquals(3, table.size());
        row.setColumnAt(0, 1);
        table.put("1", row);
        Assert.assertEquals(3, table.size());
        table.remove("1");
        Assert.assertEquals(2, table.size());
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.size());
        table.commit();
    }

    @Test
    public void commitWork() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        table.put("1", row);
        table.put("2", row);
        table.put("3", row);
        Assert.assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(3, table.commit());
    }

    @Test
    public void rollBackWork() {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        table.put("new1", row);
        table.put("new2", row);
        table.put("new3", row);
        Assert.assertEquals(3, table.rollback());
        Assert.assertEquals(0, table.rollback());
        Storeable oldValue = table.put("new1", row);
        Assert.assertNull(oldValue);
        table.remove("new1");
        table.remove("new2");
        table.remove("new3");
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void countTheNumberOfChangesWork() throws IOException {
        table.commit();
        int numberOfChanges = ((DBTable) table).countTheNumberOfChanges();
        Assert.assertEquals(0, numberOfChanges);
        Storeable value = table.get("key");
        numberOfChanges = ((DBTable) table).countTheNumberOfChanges();
        Assert.assertEquals(0, numberOfChanges);
        if (value == null) {
            table.put("key", tableProvider.createFor(table));
            numberOfChanges = ((DBTable) table).countTheNumberOfChanges();
            Assert.assertEquals(1, numberOfChanges);
            table.remove("key");
            numberOfChanges = ((DBTable) table).countTheNumberOfChanges();
            Assert.assertEquals(0, numberOfChanges);
        } else {
            table.remove("key");
            numberOfChanges = ((DBTable) table).countTheNumberOfChanges();
            Assert.assertEquals(1, numberOfChanges);
        }
    }

    @Test
    public void commitRollback() throws IOException {
        Storeable newRow = new DBStoreable(columnTypes);
        newRow.setColumnAt(0, 123);
        table.put("old", newRow);
        table.put("oldWhichWillRemoved", newRow);
        table.commit();
        Assert.assertEquals(2, table.size());
        Storeable row = new DBStoreable(columnTypes);
        row.setColumnAt(0, 1);
        //overwrite
        table.put("old", row);
        Assert.assertEquals(2, table.size());
        table.put("12345", row);
        Assert.assertEquals(3, table.size());
        table.remove("oldWhichWillRemoved");
        Assert.assertEquals(2, table.size());
        table.rollback();
        Assert.assertEquals(2, table.size());
        table.remove("old");
        table.remove("oldWhichWillRemoved");
        table.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void getNameAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void getAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.get("1");
    }

    @Test(expected = IllegalStateException.class)
    public void putAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        Storeable value = tableProvider.createFor(newTable);
        ((DBTable) newTable).close();
        newTable.put("1", value);
    }

    @Test(expected = IllegalStateException.class)
    public void removeAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.remove("1");
    }

    @Test(expected = IllegalStateException.class)
    public void sizeAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.size();
    }

    @Test(expected = IllegalStateException.class)
    public void commitAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void rollbackAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void getColumnsCountAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.getColumnsCount();
    }

    @Test(expected = IllegalStateException.class)
    public void getColumnsTypeAfterCloseTableShouldFail() throws IOException {
        Table newTable = tableProvider.createTable("tableWhichWillClosed", columnTypes);
        ((DBTable) newTable).close();
        newTable.getColumnType(0);
    }
}
