package ru.fizteh.fivt.students.valentinbarishev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MyParallelTest {
    private Table table;
    static TableProviderFactory factory;
    private TableProvider provider;
    static List<Class<?>> types;
    private ThreadPoolExecutor pool;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);
    }

    @Before
    public void beforeTest() throws IOException {
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());
        table = provider.createTable("parallel", types);

        ThreadFactory demonFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        };

        pool = new ThreadPoolExecutor(1, 10, Long.MAX_VALUE, TimeUnit.NANOSECONDS,
                new LinkedBlockingQueue<Runnable>(), demonFactory);

    }

    @After
    public void afterTest() {
        pool.shutdownNow();
    }

    public void storeableEquals(Storeable a, Storeable b) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Assert.assertEquals(a.getColumnAt(i), b.getColumnAt(i));
        }
    }

    @Test(timeout = 15000)
    public void concurrentPutCommitAndGet() throws Exception {
        final List<String> keyList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            keyList.add(Integer.toString(i));
        }

        Future<Object> firstHalf = pool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (int i = 0; i < keyList.size() / 2; ++i) {
                    String key = keyList.get(i);
                    Assert.assertNull(table.put(key, provider.createFor(table, Arrays.asList(key, i))));
                    Assert.assertEquals(1, table.commit());
                }
                return null;
            }
        });

        Future<Object> secondHalf = pool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (int i = keyList.size() / 2; i < keyList.size(); ++i) {
                    String key = keyList.get(i);
                    Assert.assertNull(table.put(key, provider.createFor(table, Arrays.asList(key, i))));
                    Assert.assertEquals(1, table.commit());
                }
                return null;
            }
        });

        firstHalf.get();
        secondHalf.get();

        for (int i = 0; i < keyList.size(); ++i) {
            String key = keyList.get(i);
            storeableEquals(table.get(key), provider.createFor(table, Arrays.asList(key, i)));
        }
    }

    @Test(timeout = 15000)
    public void concurrentPutBigCommitAndGet() throws Exception {
        final List<String> keyList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            keyList.add(Integer.toString(i));
        }

        Future<Object> firstHalf = pool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (int i = 0; i < keyList.size() / 2; ++i) {
                    String key = keyList.get(i);
                    Assert.assertNull(table.put(key, provider.createFor(table, Arrays.asList(key, i))));
                    Assert.assertEquals(1, table.commit());
                }
                return null;
            }
        });

        Future<Object> secondHalf = pool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (int i = keyList.size() / 2; i < keyList.size(); ++i) {
                    String key = keyList.get(i);
                    Assert.assertNull(table.put(key, provider.createFor(table, Arrays.asList(key, i))));
                }
                Assert.assertEquals(keyList.size() - keyList.size() / 2, table.commit());
                return null;
            }
        });

        firstHalf.get();
        secondHalf.get();

        for (int i = 0; i < keyList.size(); ++i) {
            String key = keyList.get(i);
            storeableEquals(table.get(key), provider.createFor(table, Arrays.asList(key, i)));
        }
    }

    @Test(timeout = 15000)
    public void concurrentCreateTables() throws Exception {

        final AtomicInteger numberNulls = new AtomicInteger(0);
        List<Future<Object>> threads = new ArrayList<>();

        for (int i = 0; i < 100; ++i) {
            Future<Object> some = pool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    if (provider.createTable("concurrent", types) == null) {
                        numberNulls.getAndIncrement();
                    }
                    return null;
                }
            });
            threads.add(some);
        }
        for (int i = 0; i < threads.size(); ++i) {
            threads.get(i).get();
        }

        Assert.assertEquals(numberNulls.get(), threads.size() - 1);
    }
}
