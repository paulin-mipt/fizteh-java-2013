package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class TestsDatabaseTable {
    private static final String SINGLE_COLUMN_TABLE_NAME = "testTable";
    private static final String MULTI_COLUMN_TABLE_NAME = "MultiColumnTable";
    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    static List<Class<?>> columnTypes;
    static List<Class<?>> columnMultiTypes;
    Table table;
    Table multiColumnTable;
    static TableProviderFactory factory;
    TableProvider provider;
    private static Storeable value1;
    private static Storeable value2;
    private AtomicInteger counter;
    ExecutorService executor;


    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws IOException {
        columnTypes = new ArrayList<Class<?>>() {
            {
                add(Integer.class);
            }
        };
        columnMultiTypes = new ArrayList<Class<?>>() {
            {
                add(Integer.class);
                add(String.class);
                add(Double.class);
            }
        };
        factory = new DatabaseTableProviderFactory();

    }

    @Before
    public void beforeTest() throws IOException {
        provider = factory.create(folder.getRoot().getPath());
        table = provider.createTable(SINGLE_COLUMN_TABLE_NAME, columnTypes);
        multiColumnTable = provider.createTable(MULTI_COLUMN_TABLE_NAME, columnMultiTypes);
        executor = Executors.newFixedThreadPool(CORE_COUNT);
    }

    @After
    public void afterTest() throws IOException {
        provider.removeTable(SINGLE_COLUMN_TABLE_NAME);
        provider.removeTable(MULTI_COLUMN_TABLE_NAME);
        if (!executor.shutdownNow().isEmpty()) {
            Assert.fail("Task queue is not empty");
        }
    }

    public Storeable makeStoreable(int value) {
        try {
            return provider.deserialize(table, String.format("<row><col>%d</col></row>", value));
        } catch (ParseException e) {
            return null;
        }
    }

    public Storeable makeMultiStoreable(int value, String valueString, Double valueDouble) {
        try {
            return provider.deserialize(multiColumnTable,
                    "<row><col>" + value + "</col><col>" + valueString + "</col><col>" + valueDouble + "</col></row>");
        } catch (ParseException e) {
            return null;
        }
    }

    @Test
    public void testPutWithNulls() throws Exception {
        table.put("brandnewrandomkey", provider.deserialize(table, "<row><null></null></row>"));
        List<Object> values = new ArrayList<Object>() {
            {
                add(null);
            }
        };
        Storeable st = provider.createFor(table, values);
        table.put("SADASDASD", st);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyNull() {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() {
        table.put("", makeStoreable(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyWithWhiteSpaces() {
        table.put("key key key", makeStoreable(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullName() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullName() {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmptyName() {
        table.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyName() {
        table.remove("");
    }

    @Test
    public void testPutGet() {
        table.put("key", makeStoreable(1));
        Assert.assertNotNull(table.put("key", makeStoreable(2)));
        table.put("key", makeStoreable(3));
        Assert.assertEquals(table.get("key"), makeStoreable(3));
        table.remove("key");
    }

    @Test
    public void testPutGetRemove() {
        Assert.assertNull(table.put("key", makeStoreable(1)));
        table.remove("key");
        Assert.assertNull(table.put("key", makeStoreable(1)));
        Assert.assertEquals(table.get("key"), makeStoreable(1));
        table.remove("key");
        Assert.assertEquals(table.get("key"), null);
    }

    @Test
    public void testMultiPutGetRemove() {
        multiColumnTable.put("ключ", makeMultiStoreable(1, "значение1", 1.1));
        multiColumnTable.remove("ключ");
        Assert.assertNull(multiColumnTable.put("ключ", makeMultiStoreable(1, "значение1", 1.1)));
        multiColumnTable.remove("ключ");
        Assert.assertNull(multiColumnTable.get("ключ"));
    }

    @Test
    public void testMultiWork() {
        multiColumnTable.put("key", makeMultiStoreable(1, "value", 1.1));
        multiColumnTable.put("key", makeMultiStoreable(2, "value2", 2.2));
        multiColumnTable.put("key_extra", makeMultiStoreable(3, "value3", 3.3));
        try {
            Assert.assertEquals(multiColumnTable.commit(), 2);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void testMultiRollback() {
        multiColumnTable.put("key", makeMultiStoreable(1, "value", 1.1));
        multiColumnTable.remove("key");
        multiColumnTable.put("key_extra", makeMultiStoreable(3, "value3", 3.3));
        multiColumnTable.remove("key_extra");
        Assert.assertEquals(multiColumnTable.rollback(), 0);
    }

    @Test
    public void firstTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key1", makeStoreable(1));
        table.put("key2", makeStoreable(2));
        table.put("key3", makeStoreable(3));
        table.remove("key3");
        Assert.assertEquals(table.size(), 2);
        try {
            Assert.assertEquals(table.commit(), 2);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void secondTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.put("key", makeStoreable(2));
        table.put("key", makeStoreable(3));
        Assert.assertEquals(table.size(), 1);
        try {
            Assert.assertEquals(table.commit(), 1);
            table.remove("key");
            Assert.assertEquals(table.size(), 0);
            Assert.assertEquals(table.commit(), 1);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void thirdTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.remove("key");
        table.put("key2", makeStoreable(2));
        table.remove("key2");
        Assert.assertNull(table.get("key2"));
        Assert.assertEquals(table.size(), 0);
        try {
            Assert.assertEquals(table.commit(), 0);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void firstTestRollback() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.put("key2", makeStoreable(2));
        table.put("key3", makeStoreable(3));
        Assert.assertEquals(table.rollback(), 3);
    }

    @Test
    public void secondTestRollback() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.remove("key");
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.rollback(), 0);
        table.put("key2", makeStoreable(2));
        try {
            Assert.assertEquals(table.commit(), 1);
        } catch (IOException e) {
            //
        }
        table.put("key2", makeStoreable(3));
        table.put("key2", makeStoreable(2));
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(0, table.rollback());
    }

    @Test
    public void getColumnsCountTest() {
        Assert.assertEquals(table.getColumnsCount(), 1);
        Assert.assertEquals(multiColumnTable.getColumnsCount(), 3);
    }

    @Test
    public void getColumnTypeTest() {
        Assert.assertEquals(table.getColumnType(0), Integer.class);
        Assert.assertEquals(multiColumnTable.getColumnType(0), Integer.class);
        Assert.assertEquals(multiColumnTable.getColumnType(1), String.class);
        Assert.assertEquals(multiColumnTable.getColumnType(2), Double.class);
    }

    @Test
    public void testShortNull() throws Exception {
        table.put("key", provider.deserialize(table, "<row><null/></row>"));
    }

    @Test
    public void commonTest() {
        Assert.assertEquals(table.size(), 0);
        Assert.assertNull(table.put("1", makeStoreable(1)));
        Assert.assertNull(table.put("2", makeStoreable(2)));
        Assert.assertNull(table.put("3", makeStoreable(4)));
        Assert.assertNotNull(table.put("3", makeStoreable(3)));
        Assert.assertEquals(table.size(), 3);
        try {
            Assert.assertEquals(table.commit(), 3);
        } catch (IOException e) {
            //
        }
        table.remove("1");
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.rollback(), 3);
        Assert.assertEquals(table.size(), 3);
    }

    @Test
    public void goodTest() {
        Assert.assertNull(table.put("111", makeStoreable(1)));
        table.remove("111");
        Assert.assertEquals(table.rollback(), 0);
    }

    //Threads
    @Test
    public void testThreadPutSizeOne() throws Exception {
        Thread onePutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Assert.assertNull(table.put("1", provider.deserialize(table, "<row><col>5</col></row>")));
                } catch (ParseException e) {
                    //
                }
            }
        });
        Thread secondPutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Assert.assertNull(table.put("2", provider.deserialize(table, "<row><col>5</col></row>")));
                } catch (ParseException e) {
                    //
                }
            }
        });
        onePutThread.start();
        secondPutThread.start();
        onePutThread.join();
        secondPutThread.join();
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void testThreadPutSizeTwo() throws Exception {
        Thread onePutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    value1 = table.put("1", provider.deserialize(table, "<row><col>5</col></row>"));
                    Assert.assertEquals(table.commit(), 1);
                } catch (ParseException e) {
                    //
                } catch (IOException e) {
                    //
                }
            }
        });
        Thread secondPutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    value2 = table.put("2", provider.deserialize(table, "<row><col>15</col></row>"));
                    Assert.assertEquals(table.commit(), 1);
                } catch (ParseException e) {
                    //
                } catch (IOException e) {
                    //
                }
            }
        });
        onePutThread.start();
        secondPutThread.start();
        onePutThread.join();
        secondPutThread.join();
        Assert.assertNull(value1);
        Assert.assertNull(value2);
        Assert.assertEquals(2, table.size());
    }

    @Test
    public void testThreadSamePut() throws Exception {
        counter = new AtomicInteger(0);
        Thread onePutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("3", provider.deserialize(table, "<row><col>5</col></row>"));
                    counter.getAndAdd(table.commit());
                } catch (ParseException e) {
                    //
                } catch (IOException e) {
                    //
                }
            }
        });
        Thread secondPutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table.put("3", provider.deserialize(table, "<row><col>5</col></row>"));
                    counter.getAndAdd(table.commit());
                } catch (ParseException e) {
                    //
                } catch (IOException e) {
                    //
                }
            }
        });
        onePutThread.start();
        secondPutThread.start();
        onePutThread.join();
        secondPutThread.join();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void multiThreadPutTest() throws Exception {
        List<Future<Storeable>> futures = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Future<Storeable> future = executor.submit(new Callable<Storeable>() {
                @Override
                public Storeable call() throws Exception {
                    table.put("key", makeStoreable(0));
                    return table.put("key", makeStoreable((int) (Math.random())));
                }
            });
            futures.add(future);
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertNotNull(futures.get(i).get());
        }
    }

    @Test
    public void multiThreadPutGetTest() throws Exception {
        List<Future<Storeable>> futures = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Future<Storeable> future = executor.submit(new Callable<Storeable>() {
                @Override
                public Storeable call() throws Exception {
                    table.put("key", makeStoreable((int) (Math.random())));
                    Storeable result = table.get("key");
                    table.remove("key");
                    return result;
                }
            });
            futures.add(future);
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertNotNull(futures.get(i).get());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testClosePutGet() throws Exception {
        DatabaseTable testTable = (DatabaseTable) table;
        testTable.put("key", makeStoreable(5));
        testTable.close();
        testTable.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseCommit() throws Exception {
        DatabaseTable testTable = (DatabaseTable) table;
        testTable.put("key1", makeStoreable(1));
        testTable.put("key2", makeStoreable(2));
        testTable.put("key3", makeStoreable(3));
        testTable.close();
        testTable.get("key1");
        testTable.get("key1");
        testTable.get("key1");
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals(table.toString(), String.format("DatabaseTable[%s]", (folder.getRoot().getPath()
                + File.separator + SINGLE_COLUMN_TABLE_NAME)));
    }
}
