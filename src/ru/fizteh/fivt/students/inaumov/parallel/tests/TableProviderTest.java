package ru.fizteh.fivt.students.inaumov.parallel.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TableProviderTest {
    private static final String DATABASE_DIRECTORY = "./parallel_test";
    private static final int THREADS_NUMBER = 5;
    private TableProvider tableProvider;
    private AtomicInteger counter = new AtomicInteger(0);
    private Table table;

    @Before
    public void setup() throws Exception {
        TableProviderFactory tableProviderFactory = new DatabaseTableProviderFactory();
        tableProvider = tableProviderFactory.create(DATABASE_DIRECTORY);
    }

    @After
    public void after() throws Exception {
        if (tableProvider.getTable("newTable") != null) {
            tableProvider.removeTable("newTable");
        }
        if (tableProvider.getTable("xTable") != null) {
            tableProvider.removeTable("xTable");
        }

        tableProvider = null;
    }

    @Test
    public void testCreateTable() throws Exception {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < THREADS_NUMBER; ++i) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    createTableAndCount();
                }
            }));
            threads.get(i).start();
        }
        createTableAndCount();
        for (Thread thread: threads) {
            thread.join();
        }
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testOneCreateEveryoneGet() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                createTable();
            }
        });
        thread.join();
        for (int i = 0; i < THREADS_NUMBER; ++i) {
            Thread thread1 = new Thread(new Runnable(){
                @Override
            public void run() {
                    Assert.assertEquals(table, tableProvider.getTable("xTable"));
                }
            });
            thread1.start();

            Assert.assertEquals(table, tableProvider.getTable("xTable"));
        }
    }

    private void createTable() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        try {
            table = tableProvider.createTable("xTable", columnTypes);
        } catch (IOException e) {
            //
        }
    }

    private void createTableAndCount() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(500) + 1000);
        } catch (InterruptedException e) {
            return;
        }

        try {
            if (tableProvider.createTable("newTable", columnTypes) != null) {
                counter.getAndIncrement();
            }
        } catch (IOException e) {
            //
        }
    }
}
