package ru.fizteh.fivt.students.kislenko.parallels.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.junit.test.Cleaner;
import ru.fizteh.fivt.students.kislenko.storeable.MyTable;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MyTableProviderTest {
    private static MyTableProvider provider;
    private static File databaseDir = new File("database");
    private static ArrayList<Class<?>> typeList = new ArrayList<Class<?>>();
    private static boolean multiThreadCorrectFlagFirst = false;
    private static boolean multiThreadCorrectFlagSecond = false;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        databaseDir.mkdir();
        provider = factory.create("database");
        typeList.add(Integer.class);
        typeList.add(Integer.class);
        typeList.add(Integer.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Cleaner.clean(databaseDir);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyNameTable() throws Exception {
        provider.createTable("", typeList);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableWithDot() throws Exception {
        provider.createTable(".", typeList);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableWithDots() throws Exception {
        provider.createTable("..", typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullNameTable() throws Exception {
        provider.createTable(null, typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTypeListTable() throws Exception {
        provider.createTable("goodTable", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithBadTypeListTable() throws Exception {
        ArrayList<Class<?>> badTypeList = new ArrayList<Class<?>>();
        badTypeList.add(StringBuilder.class);
        provider.createTable("tableBetterThanTheLastOne", badTypeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyTypeListTable() throws Exception {
        ArrayList<Class<?>> empty = new ArrayList<Class<?>>();
        provider.createTable("theBestTable", empty);
    }

    @Test
    public void testCreateNormalTable() throws Exception {
        provider.createTable("definitelyTheBestTable", typeList);
    }

    @Test
    public void testCreateExistingTable() throws Exception {
        provider.createTable("definitelyTheBestTable", typeList);
    }

    @Test
    public void testMultiThreadCreateSameTables() throws Exception {
        multiThreadCorrectFlagFirst = false;
        multiThreadCorrectFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagFirst = (provider.createTable("myFirstParallelTable", typeList) == null);
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagSecond = (provider.createTable("myFirstParallelTable", typeList) == null);
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        provider.removeTable("myFirstParallelTable");
        Assert.assertTrue(multiThreadCorrectFlagFirst ^ multiThreadCorrectFlagSecond);
    }

    @Test
    public void testMultiThreadCreateDifferentTables() throws Exception {
        multiThreadCorrectFlagFirst = false;
        multiThreadCorrectFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagFirst = (provider.createTable("myFirstParallelTable", typeList) != null);
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagSecond = (provider.createTable("mySecondParallelTable", typeList) != null);
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertTrue(multiThreadCorrectFlagFirst && multiThreadCorrectFlagSecond);
        provider.removeTable("myFirstParallelTable");
        provider.removeTable("mySecondParallelTable");
    }

    @Test
    public void testMultiThreadCreateRemoveSameTables() throws Exception {
        multiThreadCorrectFlagFirst = false;
        multiThreadCorrectFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagFirst = (provider.createTable("parallel", typeList) != null);
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.removeTable("parallel");
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertNull(provider.getTable("parallel"));
    }

    @Test
    public void testMultiThreadCreateGetSameTables() throws Exception {
        final AtomicReference<MyTable> ref1 = new AtomicReference<MyTable>();
        final AtomicReference<MyTable> ref2 = new AtomicReference<MyTable>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ref1.set(provider.createTable("KRESLO", typeList));
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                ref2.set(provider.getTable("KRESLO"));
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(ref1.get(), ref2.get());
        provider.removeTable("KRESLO");
    }

    @Test
    public void testMultiThreadCreateRemoveGetSameTables() throws Exception {
        final AtomicReference<MyTable> ref1 = new AtomicReference<MyTable>();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.createTable("KRESLO", typeList);
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.removeTable("KRESLO");
                } catch (IOException ignored) {
                    // This block isn't empty.
                }
            }
        });
        Thread third = new Thread(new Runnable() {
            @Override
            public void run() {
                ref1.set(provider.getTable("KRESLO"));
            }
        });
        first.start();
        second.start();
        third.start();
        first.join();
        second.join();
        third.join();
        Assert.assertNull(ref1.get());
    }
}
