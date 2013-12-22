package ru.fizteh.fivt.students.inaumov.parallel.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProvider;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TableTest {
    private static final int THREADS_NUMBER = 10;
    private static final int KEYS_COUNT = 5;
    private static final String DATABASE_DIRECTORY = "./parallel_test";

    private TableProvider tableProvider;
    private Table currentTable;

    private AtomicInteger counter = new AtomicInteger(0);

    @Before
    public void setup() throws Exception {
        TableProviderFactory tableProviderFactory = new DatabaseTableProviderFactory();
        tableProvider = tableProviderFactory.create(DATABASE_DIRECTORY);
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        currentTable = tableProvider.createTable("Table", columnTypes);
    }

    @After
    public void after() throws Exception {
        tableProvider.removeTable("Table");
    }

    @Test
    public void testCurrentOnlyThreadDiff() throws Exception {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < THREADS_NUMBER; ++i) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        checkThreadOnlyDiff();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }));
            threads.get(i).start();
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                return;
            }
        }

        Assert.assertEquals(THREADS_NUMBER * KEYS_COUNT, counter.get());
    }

    private void checkThreadOnlyDiff() throws Exception {
        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            currentTable.put(key, makeStoreable(i));
        }

        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            Storeable value1 = makeStoreable(i);
            Storeable value2 = currentTable.get(key);
            if (value1 == null || value2 == null) {
                throw new Exception();
            }
            if (value1.equals(value2)) {
                counter.getAndIncrement();
            }
        }
    }

    private Storeable makeStoreable(int value) {
        String string = "[\"value" + Thread.currentThread().getId() + value + "\"]";
        try {
            return tableProvider.deserialize(currentTable, string);
        } catch (ParseException e) {
            return null;
        }
    }
}
