package ru.fizteh.fivt.students.vlmazlov.parallel.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableParallelProviderTest {
    private volatile StoreableTableProvider provider;
    private static Table table;
    private List<Class<?>> valueTypes1;
    private List<Class<?>> valueTypes2;
    private List<Object> values1;
    private List<Object> values2;
    private List<Object> values3;
    private List<Object> values4;
    private final String root = "StoreableTableTest";

    @Before
    public void setUp() {
        try {
            File tempDir = FileUtils.createTempDir(root, null);
            provider = new StoreableTableProvider(tempDir.getPath(), false);
            valueTypes1 = new ArrayList<Class<?>>() { {
                add(Double.class);
                add(Integer.class);
                add(Boolean.class);
            }};

            valueTypes2 = new ArrayList<Class<?>>() { {
                add(String.class);
                add(Float.class);
                add(Boolean.class);
            }};

            values1 = new ArrayList<Object>() { {
                add(Double.valueOf("1.54"));
                add(Integer.valueOf("123412"));
                add(Boolean.valueOf("false"));
            }};

            values2 = new ArrayList<Object>() { {
                add(Float.valueOf("1.5f"));
                add(new String("123412"));
                add(Boolean.valueOf("false"));
            }};

            values3 = new ArrayList<Object>() { {
                add(Boolean.valueOf("false"));
            }};

            values4 = new ArrayList<Object>() { {
                add(Float.valueOf("1.5f"));
                add(new String("123412"));
                add(Boolean.valueOf("false"));
                add(Double.valueOf("1.54"));
            }};

        } catch (ValidityCheckFailedException ex) {
            Assert.fail("validity check failed: " + ex.getMessage());
        }
    }

    @Test
    public void gettingCreatedInAnotherThread() throws IOException {

        Thread testThread = new Thread() {
            @Override
            public void run() {
                try {
                    table = provider.createTable("testGet", valueTypes1);
                } catch (IOException ex) {
                    Assert.fail("unable to create table");
                }
            }
        };

        testThread.start();
        try {
            testThread.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        Table firstGet = provider.getTable("testGet");
        Table secondGet = provider.getTable("testGet");

        Assert.assertNull("table should not be created twice", provider.createTable("testGet", valueTypes1));
        Assert.assertSame("getting should returns the same table as create", table, firstGet);
        Assert.assertSame("getting the same table twice should return the same", firstGet, secondGet);
        provider.removeTable("testGet");
    }

    @Test
    public void gettingRemovedInAnotherThread() throws IOException {

        try {
            provider.createTable("testGet", valueTypes1);
        } catch (IOException ex) {
            Assert.fail("unable to create table");
        }

        Thread testThread = new Thread() {
            @Override
            public void run() {
                provider.removeTable("testGet");
            }
        };

        testThread.start();
        try {
            testThread.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        Table getRemoved = provider.getTable("testGet");

        Assert.assertNull("table was removed in another thread", getRemoved);
    }
}
