package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;

import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTable;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class FileMapTableTest {
    TableProviderFactory factory;
    TableProvider tableProvider;
    Table currTable;
    String currTableName;
    String path;
    List<Class<?>> classes;
    String value = "[0,1,2,3,4,5.4,false,\"string1\",\"string2\"]";

    Table table;
    String value1;
    Storeable value1Storeable;
    String value2;
    Storeable value2Storeable;
    String value3;
    Storeable value3Storeable;
    String value4;
    Storeable value4Storeable;
    String value5;
    Storeable value5Storeable;

    Thread first;
    Thread second;
    Thread third;

    private boolean storeableEquals(Storeable first, Storeable second, Table table) {
        if (first == null && second == null) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Object val1 = first.getColumnAt(i);
            Object val2 = second.getColumnAt(i);
            if (val1 == null) {
                if (val2 != null) {
                    return false;
                }
            } else if (!val1.equals(val2)) {
                return false;
            }
        }
        return true;
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setCurrTable() throws IOException, ParseException {
        factory = new FileMapTableProviderFactory();
        classes = new ArrayList<Class<?>>();
        classes.add(Integer.class);
        classes.add(Integer.class);
        classes.add(Integer.class);
        classes.add(Long.class);
        classes.add(Float.class);
        classes.add(Double.class);
        classes.add(Boolean.class);
        classes.add(String.class);
        classes.add(String.class);
        path = folder.newFolder().toString();
        tableProvider = factory.create(path);
        assertNotNull(tableProvider);
        currTable = tableProvider.createTable("TestTable", classes);
        currTableName = "TestTable";
        assertEquals(currTable.getName(), currTableName);
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        table = tableProvider.createTable("table", classList);
        value1 = "[1,\"value1\"]";
        value1Storeable = tableProvider.deserialize(table, value1);
        assertEquals(tableProvider.serialize(table, value1Storeable), value1);
        value2 = "[2,\"value2\"]";
        value2Storeable = tableProvider.deserialize(table, value2);
        assertEquals(tableProvider.serialize(table, value2Storeable), value2);
        value3 = "[3,\"value3\"]";
        value3Storeable = tableProvider.deserialize(table, value3);
        assertEquals(tableProvider.serialize(table, value3Storeable), value3);
        value4 = "[4,\"value4\"]";
        value4Storeable = tableProvider.deserialize(table, value4);
        assertEquals(tableProvider.serialize(table, value4Storeable), value4);
        value5 = "[5,\"value5\"]";
        value5Storeable = tableProvider.deserialize(table, value5);
        assertEquals(tableProvider.serialize(table, value5Storeable), value5);
    }

    @After
    public void delete() throws IOException {
        tableProvider.removeTable(currTableName);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(currTableName, currTable.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws ParseException {
        currTable.put(null, tableProvider.deserialize(currTable, value));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        currTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws ParseException {
        currTable.put("    ", tableProvider.deserialize(currTable, value));
    }

    @Test
    public void testPutNewKey() throws ParseException {
        assertNull(currTable.put("new", tableProvider.deserialize(currTable, value)));
    }

    @Test
    public void testPutChangedKey() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueOld));
        valueOld.setColumnAt(0, 1);
        assertFalse(storeableEquals(currTable.get("key"), valueOld, currTable));
    }

    @Test
    public void testPutChangedKey2() throws ParseException, IOException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueOld));
        valueOld.setColumnAt(0, 1);
        assertEquals(currTable.commit(), 1);
        assertFalse(storeableEquals(currTable.get("key"), valueOld, currTable));
    }

    @Test
    public void testPutChangedKey3() throws ParseException, IOException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueOld));
        assertEquals(currTable.commit(), 1);
        valueOld.setColumnAt(0, 1);
        assertFalse(storeableEquals(currTable.get("key"), valueOld, currTable));
    }

    @Test
    public void testPutOldKey() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        String valueNewString = "[10,11,22,33,4.4,5,true,\"new1\",\"new2\"]";
        Storeable valueNew = tableProvider.deserialize(currTable, valueNewString);
        assertNull(currTable.put("key", valueOld));
        assertTrue(storeableEquals(currTable.put("key", valueNew), valueOld, currTable));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAlienSmallerStoreable() throws ParseException, IOException {
        String val = "[15,\"string\",\"second string\"]";
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("smallerTable", classList);
        Storeable valueOld = tableProvider.deserialize(table, val);
        tableProvider.removeTable("smallerTable");
        currTable.put("key", valueOld);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAlienBiggerStoreable() throws ParseException, IOException {
        String val = "[0,1,2,3,4,5.4,false,\"string1\",\"string2\", 1]";
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(Integer.class);
        classList.add(Integer.class);
        classList.add(Long.class);
        classList.add(Float.class);
        classList.add(Double.class);
        classList.add(Boolean.class);
        classList.add(String.class);
        classList.add(String.class);
        classList.add(Byte.class);
        Table table = tableProvider.createTable("biggerTable", classList);
        Storeable valueOld = tableProvider.deserialize(table, val);
        tableProvider.removeTable("biggerTable");
        currTable.put("key", valueOld);
    }

    @Test
    public void testPut() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        String valueNewString = "[null,null,null,null,null,null,null,null,null]";
        Storeable valueNew = tableProvider.deserialize(currTable, valueNewString);
        assertNull(currTable.put("key", valueOld));
        assertTrue(storeableEquals(currTable.put("key", valueNew), valueOld, currTable));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        currTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyKey() {
        currTable.remove(" ");
    }

    @Test
    public void testRemoveNotExistsKey() throws ParseException {
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueStoreable));
        assertNotNull(currTable.remove("key"));
        assertNull(currTable.remove("key"));
    }

    @Test
    public void testRemoveExistsKey() throws ParseException {
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        currTable.put("key", valueStoreable);
        assertTrue(storeableEquals(currTable.remove("key"), valueStoreable, currTable));
    }

    @Test
    public void testGetNotExistsKey() throws Exception {
        assertNull(currTable.get("notExistsKey"));
    }

    @Test
    public void testGetExistsKey() throws Exception {
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("newKey", valueStoreable));
        assertNotNull(currTable.get("newKey"));
        assertNotNull(currTable.remove("newKey"));
    }

    @Test
    public void testCommit() throws Exception {
        int sizeBefore = table.size();
        assertNull(table.put("key1", value1Storeable));
        assertTrue(storeableEquals(table.put("key1", value1Storeable), value1Storeable, table));
        assertNull(table.put("key2", value2Storeable));
        assertTrue(storeableEquals(table.put("key2", value2Storeable), value2Storeable, table));
        assertNull(table.put("key3", value3Storeable));
        assertTrue(storeableEquals(table.put("key3", value3Storeable), value3Storeable, table));
        assertTrue(storeableEquals(table.remove("key1"), value1Storeable, table));
        assertTrue(storeableEquals(table.put("key2", value1Storeable), value2Storeable, table));
        assertEquals(table.commit(), 2);
        int sizeAfter = table.size();
        assertEquals(2, sizeAfter - sizeBefore);
    }

    @Test
    public void testRollback() throws Exception {
        assertNull(table.put("key1", value1Storeable));
        assertNull(table.put("key2", value2Storeable));
        assertNotNull(table.put("key2", value2Storeable));
        assertTrue(storeableEquals(table.remove("key2"), value2Storeable, table));
        assertEquals(table.size(), 1);
        assertNull(table.put("key3", value3Storeable));

        assertEquals(table.size(), 2);
        assertEquals(table.rollback(), 2);
        assertEquals(table.rollback(), 0);
        assertEquals(table.commit(), 0);

        assertNull(table.put("key1", value1Storeable));
        assertNull(table.put("key2", value2Storeable));
        assertNull(table.put("key3", value3Storeable));

        assertEquals(table.size(), 3);
        assertEquals(table.commit(), 3);
        assertEquals(table.rollback(), 0);
        assertEquals(table.size(), 3);

        assertTrue(storeableEquals(table.put("key1", value2Storeable), value1Storeable, table));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.remove("key1"), value2Storeable, table));
        assertNull(table.remove("key1"));
        assertEquals(table.size(), 2);
        assertNull(table.put("key4", value4Storeable));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.put("key3", value5Storeable), value3Storeable, table));
        assertTrue(storeableEquals(table.remove("key4"), value4Storeable, table));
        assertNull(table.remove("key4"));
        assertEquals(table.size(), 2);
        assertNull(table.get("key1"));
        assertNull(table.get("key4"));

        assertEquals(table.rollback(), 2);
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.get("key1"), value1Storeable, table));

        assertEquals(tableProvider.serialize(table, table.remove("key1")), value1);
        assertNull(table.put("key1", value1Storeable));
        assertEquals(table.size(), 3);

        assertEquals(table.rollback(), 0);
    }

    @Test
    public void testSize() throws Exception {
        assertNull(table.put("key", value1Storeable));
        assertTrue(storeableEquals(table.remove("key"), value1Storeable, table));
        assertNull(table.get("key"));
        assertNull(table.put("key", tableProvider.deserialize(table, value1)));
        assertTrue(storeableEquals(table.get("key"), value1Storeable, table));

        assertNull(table.put("key2", value2Storeable));
        assertTrue(storeableEquals(table.put("key2", value1Storeable), value2Storeable, table));
        assertTrue(storeableEquals(table.get("key2"), value1Storeable, table));
        assertNull(table.put("key3", value3Storeable));
        assertTrue(storeableEquals(table.put("key3", value3Storeable), value3Storeable, table));
        assertEquals(table.commit(), 3);
        assertEquals(table.size(), 3);

        assertTrue(storeableEquals(table.remove("key"), value1Storeable, table));
        assertEquals(table.size(), 2);
        assertNull(table.put("key4", value4Storeable));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.put("key2", value4Storeable), value1Storeable, table));
        assertTrue(storeableEquals(table.get("key2"), value4Storeable, table));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.remove("key4"), value4Storeable, table));
        assertEquals(table.size(), 2);
    }

    @Test
    public void testCommitRollback() throws ParseException, IOException {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        Table table1 = tableProvider.createTable("table1", classList);

        String rollbackVal = "[7,\"rollback\"]";
        Storeable rollback = tableProvider.deserialize(table1, rollbackVal);
        String rollbackVal1 = "[7,\"rollback1\"]";
        Storeable rollback1 = tableProvider.deserialize(table1, rollbackVal1);

        assertNull(table1.put("commit", rollback));
        assertTrue(storeableEquals(table1.get("commit"), rollback, table1));
        assertEquals(table1.rollback(), 1);

        assertNull(table1.get("commit"));
        assertNull(table1.put("commit", rollback));
        assertTrue(storeableEquals(table1.get("commit"), rollback, table1));
        assertEquals(table1.commit(), 1);

        assertTrue(storeableEquals(table1.remove("commit"), rollback, table1));
        assertNull(table1.put("commit", rollback1));

        assertEquals(table1.commit(), 1);
        assertTrue(storeableEquals(table1.get("commit"), rollback1, table1));
    }

    @Test
    public void testParallelsPutWithoutCommit() throws InterruptedException {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                assertNull(table.put(value1, value1Storeable));
                assertTrue(storeableEquals(table.get(value1), value1Storeable, table));
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                assertNull(table.put(value1, value1Storeable));
                assertTrue(storeableEquals(table.get(value1), value1Storeable, table));
            }
        });

        first.start();
        second.start();
        first.join();
        second.join();

        assertNull(table.get(value1));
    }

    @Test
    public void testParallelsPutWithOneCommit() throws InterruptedException {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                assertNull(table.put(value1, value1Storeable));
                assertTrue(storeableEquals(table.get(value1), value1Storeable, table));
                try {
                    assertEquals(table.commit(), 1);
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
                assertTrue(storeableEquals(table.get(value1), value1Storeable, table));
                assertTrue(storeableEquals(table.put(value1, value2Storeable), value1Storeable, table));
                try {
                    assertEquals(table.commit(), 1);
                } catch (IOException e) {
                    //
                }
            }
        });

        first.start();
        second.start();
        first.join();
        second.join();

        assertTrue(storeableEquals(table.get(value1), value2Storeable, table));
    }

    @Test
    public void myOwnTestParallelsWithCommitAnsRollbacks() throws InterruptedException {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    assertNull(table.put(value1, value1Storeable));
                    Thread.sleep(100);
                    assertTrue(storeableEquals(table.get(value1), value1Storeable, table));
                    assertEquals(table.commit(), 1);
                } catch (IOException | InterruptedException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                assertNull(table.put(value1, value1Storeable));
                try {
                    first.join();
                } catch (InterruptedException e) {
                    //
                }
                assertEquals(table.rollback(), 0);
            }
        });

        first.start();
        second.start();
        first.join();
        second.join();

        assertTrue(storeableEquals(table.get(value1), value1Storeable, table));
    }

    @Test
    public void testParallelsSimpleCommit() throws InterruptedException {
        final Storeable[] v = new Storeable[2];
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                v[0] = table.put(value1, value1Storeable);
                try {
                    table.commit();
                } catch (IOException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                v[1] = table.put(value1, value1Storeable);
                try {
                    table.commit();
                } catch (IOException e) {
                    //
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();

        assertTrue(v[1] == null || v[0] == null);
    }

    @Test
    public void testParallelsRemoveSynohronize() throws InterruptedException {
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    assertNull(table.put(value1, value1Storeable));
                    assertNull(table.put(value3, value3Storeable));
                    assertEquals(table.commit(), 2);
                    assertEquals(table.rollback(), 0);

                    assertNotNull(table.put(value1, value1Storeable));
                    second.join();
                    assertNotNull(table.remove(value1));
                    assertEquals(table.rollback(), 0);
                    assertNull(table.get(value1));
                    assertNull(table.put(value1, value1Storeable));
                } catch (IOException | InterruptedException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    assertNull(table.put(value2, value2Storeable));
                    assertEquals(table.commit(), 1);
                    assertEquals(table.rollback(), 0);

                    Thread.sleep(100);
                    assertNotNull(table.get(value1));
                    assertNotNull(table.get(value2));
                    assertNotNull(table.get(value3));
                    assertNotNull(table.remove(value1));
                    assertNotNull(table.put(value2, value4Storeable));
                    assertEquals(table.commit(), 2);
                } catch (IOException | InterruptedException e) {
                    //
                }
            }
        });

        first.start();
        second.start();
        first.join();
        second.join();

        assertNull(table.get(value1));
        assertTrue(storeableEquals(table.get(value2), value4Storeable, table));
        assertTrue(storeableEquals(table.get(value3), value3Storeable, table));
    }

    @Test
    public void testParallelsCommits() throws InterruptedException {
        table.put("key1", value1Storeable);
        try {
            table.commit();
        } catch (IOException e) {
            //
        }
        first = new Thread(new Runnable() {
            @Override
            public void run() {
                table.put("key1", value2Storeable);
                try {
                    Thread.sleep(100);
                    assertEquals(table.commit(), 1);
                } catch (IOException | InterruptedException e) {
                    //
                }
            }
        });
        second = new Thread(new Runnable() {
            @Override
            public void run() {
                table.put("key1", value1Storeable);
                table.put("key1", value1Storeable);
                try {
                    Thread.sleep(300);
                    assertEquals(table.commit(), 1);
                } catch (InterruptedException | IOException e) {
                    //
                }
            }
        });

        first.start();
        second.start();
        first.join();
        second.join();
    }

    @Test
    public void testDoubleCloseTable() throws ParseException {
        FileMapTable fileMapTable = (FileMapTable) table;
        assertNull(fileMapTable.put("key", value1Storeable));
        fileMapTable.close();
        fileMapTable.close();
    }

    @Test(expected = IllegalStateException.class)
    public void testPutAfterClose() {
        FileMapTable fileMapTable = (FileMapTable) table;
        assertNull(fileMapTable.put("key", value1Storeable));
        fileMapTable.close();
        fileMapTable.put("key1", value1Storeable);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAfterClose() {
        FileMapTable fileMapTable = (FileMapTable) table;
        assertNull(fileMapTable.put("key", value1Storeable));
        fileMapTable.close();
        fileMapTable.get("key1");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAfterClose() {
        FileMapTable fileMapTable = (FileMapTable) table;
        assertNull(fileMapTable.put("key", value1Storeable));
        fileMapTable.close();
        fileMapTable.remove("key1");
    }

    @Test(expected = IllegalStateException.class)
    public void testCommitAfterClose() {
        FileMapTable fileMapTable = (FileMapTable) table;
        assertNull(fileMapTable.put("key", value1Storeable));
        fileMapTable.close();
        fileMapTable.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testRollbackAfterClose() {
        FileMapTable fileMapTable = (FileMapTable) table;
        assertNull(fileMapTable.put("key", value1Storeable));
        fileMapTable.close();
        fileMapTable.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void testSizeAfterClose() {
        FileMapTable fileMapTable = (FileMapTable) table;
        assertNull(fileMapTable.put("key", value1Storeable));
        fileMapTable.close();
        fileMapTable.size();
    }

    @Test
    public void testToString() {
        String str = table.toString();
        assertEquals(str, "FileMapTable[" + path + File.separator + "table]");
    }

    @Test
    public void getTableAfterClose() throws IOException {
        Table table = tableProvider.createTable("closedTable", classes);
        ((FileMapTable) table).close();
        assertNotSame(table, tableProvider.getTable("closedTable"));
    }
}
