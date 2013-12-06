package ru.fizteh.fivt.students.eltyshev.parallel.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeTableTest {
    private static final int THREAD_COUNT = 2;
    private static final int KEYS_COUNT = 5;
    private static String DATABASE_DIRECTORY = "C:\\temp\\storeable_test";
    private static String TABLE_NAME = "ThreadSafeTable";

    private TableProvider provider;
    private Table currentTable;

    private AtomicInteger correctCounter = new AtomicInteger(0);

    @Before
    public void setUp() throws Exception {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        provider = factory.create(DATABASE_DIRECTORY);
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        currentTable = provider.createTable(TABLE_NAME, columnTypes);
    }

    @After
    public void tearDown() throws Exception {
        provider.removeTable(TABLE_NAME);
    }

    @Test
    public void testCurrentOnlyThreadDiff() {
        List<Thread> threads = new ArrayList<>();
        for (int index = 0; index < THREAD_COUNT; ++index) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    checkThreadOnlyDiff();
                }
            }));
            threads.get(index).start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                return;
            }
        }
        Assert.assertEquals(THREAD_COUNT * KEYS_COUNT, correctCounter.get());
    }

    private void checkThreadOnlyDiff() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            currentTable.put(key, makeStoreable(index));
        }

        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            Storeable value1 = makeStoreable(index);
            Storeable value2 = currentTable.get(key);
            if (value1 == null || value2 == null) {
                System.out.println("BOOOM");
            }
            if (value1.equals(value2)) {
                correctCounter.getAndIncrement();
            }
        }
    }

    private Storeable makeStoreable(int value1) {
        String xml = String.format("<row><col>value%d%d</col></row>", Thread.currentThread().getId(), value1);
        try {
            return provider.deserialize(currentTable, xml);
        } catch (ParseException e) {
            return null;
        }
    }
}
