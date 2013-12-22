package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTable;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProvider;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;
import ru.fizteh.fivt.students.anastasyev.filemap.MyStoreable;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class FileMapTableProviderTest {
    TableProviderFactory tableProviderFactory;
    String path;
    TableProvider tableProvider;
    List<Class<?>> types;
    List<Class<?>> classes;
    Table firstParallelTable;
    Table secondParallelTable;
    Thread first;
    Thread second;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setTableProvider() throws IOException {
        tableProviderFactory = new FileMapTableProviderFactory();
        path = folder.newFolder().toString();
        tableProvider = tableProviderFactory.create(path);
        assertNotNull(tableProvider);
        classes = new ArrayList<Class<?>>();
        classes.add(Integer.class);
        classes.add(String.class);

        types = new ArrayList<Class<?>>();
        types.add(Integer.class);
        types.add(Long.class);
        types.add(Byte.class);
        types.add(Float.class);
        types.add(Double.class);
        types.add(String.class);
    }

    @Test
    public void testCreateTable() throws IOException {
        assertNotNull(tableProvider.createTable("testTable", classes));
        assertNull(tableProvider.createTable("testTable", classes));
        tableProvider.removeTable("testTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullNamedTable() throws IOException {
        tableProvider.createTable(null, classes);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateBadNamedTable() throws IOException {
        tableProvider.createTable("gew?ge>", classes);
    }

    @Test
    public void testGetTable() throws IOException {
        Table table = tableProvider.createTable("testGetTable", classes);
        assertNotNull(tableProvider.getTable("testGetTable"));
        assertNull(tableProvider.getTable("testTableNotExists"));
        assertEquals(table, tableProvider.getTable("testGetTable"));
        tableProvider.removeTable("testGetTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullNamedTable() {
        tableProvider.getTable(null);
    }

    @Test
    public void testRemoveExistsTable() throws IOException {
        assertNotNull(tableProvider.createTable("existsTable", classes));
        tableProvider.removeTable("existsTable");
        assertNull(tableProvider.getTable("existsTable"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistsTable() throws IOException {
        tableProvider.removeTable("notExistsTable");
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveBadNamedTable() throws IOException {
        tableProvider.removeTable("?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableEmptyColumnTypesShouldFail() throws IOException {
        List<Class<?>> badClasses = new ArrayList<Class<?>>();
        badClasses.add(Integer.class);
        badClasses.add(String.class);
        badClasses.add(null);
        tableProvider.createTable("table", badClasses);
    }

    @Test
    public void serializeTest() throws IOException, ParseException {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Double.class);
        classList.add(Float.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);
        Storeable storeable = new MyStoreable(table);
        storeable.setColumnAt(0, null);
        assertEquals(storeable.getDoubleAt(0), null);
        storeable.setColumnAt(1, ((Double) 5.505).floatValue());
        assertEquals(storeable.getFloatAt(1), ((Double) 5.505).floatValue());
        storeable.setColumnAt(2, "val");
        assertEquals(storeable.getStringAt(2), "val");
        String serialize = tableProvider.serialize(table, storeable);
        assertEquals(serialize, "[null,5.505,\"val\"]");
        tableProvider.removeTable("table");
    }

    @Test
    public void deserializeSerializeTest() throws IOException, ParseException {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Float.class);
        classList.add(Double.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);
        String compositValue = "[1.5,2.5,\"val\"]";
        Storeable compositValueStoreable = tableProvider.deserialize(table, compositValue);
        assertEquals(tableProvider.serialize(table, compositValueStoreable), compositValue);
        assertEquals(compositValueStoreable.getFloatAt(0), ((Double) 1.5).floatValue());
        assertEquals(compositValueStoreable.getDoubleAt(1), 2.5);
        assertEquals(compositValueStoreable.getStringAt(2), "val");
        tableProvider.removeTable("table");
    }

    @Test
    public void createForTest() throws IOException {
        Table table = tableProvider.createTable("createForTable", types);

        List<Object> values = new ArrayList<Object>();
        values.add((Object) 1);
        values.add((Object) 2);
        values.add((Object) 3);
        values.add((Object) 4.5);
        values.add((Object) 5);
        values.add((Object) "string");
        Storeable storeable = tableProvider.createFor(table, values);

        assertEquals(storeable.getIntAt(0), (Integer) 1);
        assertEquals(storeable.getLongAt(1), (Long) ((Integer) 2).longValue());
        assertEquals(storeable.getByteAt(2), (Byte) ((Integer) 3).byteValue());
        assertEquals(storeable.getFloatAt(3), ((Double) 4.5).floatValue());
        assertEquals(storeable.getDoubleAt(4), ((Integer) 5).doubleValue());
        assertEquals(storeable.getStringAt(5), "string");
        tableProvider.removeTable("createForTable");
    }

    @Test
    public void testParallelsCreateSameTables() throws Exception {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    firstParallelTable = tableProvider.createTable("parallelTable", types);
                } catch (IOException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    secondParallelTable = tableProvider.createTable("parallelTable", types);
                } catch (IOException e) {
                    //
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        assertTrue(firstParallelTable == null || secondParallelTable == null);
        tableProvider.removeTable("parallelTable");
    }

    @Test
    public void testParallelsCreateDifferentTables() throws Exception {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    firstParallelTable = tableProvider.createTable("firstTable", types);
                } catch (IOException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    secondParallelTable = tableProvider.createTable("secondTable", types);
                } catch (IOException e) {
                    //
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();

        assertTrue(firstParallelTable != null && secondParallelTable != null);
        tableProvider.removeTable("firstTable");
        tableProvider.removeTable("secondTable");
    }

    @Test
    public void testParallelsCreateGet() throws InterruptedException, IOException {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    firstParallelTable = tableProvider.createTable("table", types);
                } catch (IOException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    first.join();
                } catch (InterruptedException e) {
                    //
                }
                secondParallelTable = tableProvider.getTable("table");
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();

        assertEquals(firstParallelTable, secondParallelTable);
        tableProvider.removeTable("table");
    }

    @Test
    public void testParallelsCreateRemoveCreate() throws Exception {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    firstParallelTable = tableProvider.createTable("table", types);
                    second.join();
                    Thread.sleep(100);
                    secondParallelTable = tableProvider.createTable("table", types);
                } catch (IOException | InterruptedException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    tableProvider.removeTable("table");
                } catch (InterruptedException | IOException e) {
                    //
                }
            }
        });
        first.start();
        second.start();
        second.join();
        assertNull(tableProvider.getTable("table"));
        first.join();

        assertEquals(secondParallelTable, tableProvider.getTable("table"));
        assertEquals(firstParallelTable.getName(), secondParallelTable.getName());
        tableProvider.removeTable("table");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetTableAfterClose() throws IOException {
        FileMapTableProvider fileMapTableProvider = (FileMapTableProvider) tableProvider;
        assertNotNull(fileMapTableProvider.createTable("ClosingTable", types));
        fileMapTableProvider.close();
        fileMapTableProvider.getTable("ClosingTable");
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateTableAfterClose() throws IOException {
        FileMapTableProvider fileMapTableProvider = (FileMapTableProvider) tableProvider;
        assertNotNull(fileMapTableProvider.createTable("ClosingTable", types));
        fileMapTableProvider.close();
        fileMapTableProvider.getTable("ClosingTable");
    }

    @Test(expected = IllegalStateException.class)
    public void testDeserializeAfterClose() throws IOException, ParseException {
        FileMapTableProvider fileMapTableProvider = (FileMapTableProvider) tableProvider;
        Table table = fileMapTableProvider.createTable("ClosingTable", types);
        fileMapTableProvider.close();
        fileMapTableProvider.deserialize(table, "[\"not valide string\"]");
    }

    @Test(expected = IllegalStateException.class)
    public void testPutAfterClose() throws IOException, ParseException {
        FileMapTableProvider fileMapTableProvider = (FileMapTableProvider) tableProvider;
        Table table = fileMapTableProvider.createTable("ClosingTable", types);
        Storeable storeable = fileMapTableProvider.deserialize(table, "[1,2,3,4,5,\"str\"]");
        fileMapTableProvider.close();
        table.put("key", storeable);
    }

    @Test(expected = IllegalStateException.class)
    public void testSerializeAfterClose() throws IOException, ParseException {
        FileMapTableProvider fileMapTableProvider = (FileMapTableProvider) tableProvider;
        Table table = fileMapTableProvider.createTable("ClosingTable", types);
        Storeable storeable = fileMapTableProvider.deserialize(table, "[1,2,3,4,5,\"str\"]");
        fileMapTableProvider.close();
        fileMapTableProvider.serialize(table, storeable);
    }

    @Test
    public void testToString() {
        String str = tableProvider.toString();
        assertEquals(str, "FileMapTableProvider[" + path + "]");
    }

    @Test(expected = IllegalStateException.class)
    public void testClosingOfTablesInProvider() throws IOException {
        FileMapTableProvider fileMapTableProvider = (FileMapTableProvider) tableProvider;
        FileMapTable table1 = (FileMapTable) fileMapTableProvider.createTable("newTable1", types);
        fileMapTableProvider.close();
        table1.get("key");
    }
}

