package ru.fizteh.fivt.students.vlmazlov.parallel.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableParallelTest {
    private StoreableTable table;
    private StoreableTableProvider provider;
    private Storeable val1;
    private Storeable val2;
    private Storeable val3;
    private Storeable val4;
    private Storeable val5;
    private final String root = "StoreableTableTest";

    @Before
    public void setUp() {
        try {
            File tempDir = FileUtils.createTempDir(root, null);
            provider = new StoreableTableProvider(tempDir.getPath(), false);

            List<Object> values1 = new ArrayList<Object>() { {
                add(null);
                add(new String("val1"));
                add(Byte.valueOf("-3"));
            }};

            List<Object> values2 = new ArrayList<Object>() { {
                add(125);
                add(new String("val2"));
                add(Byte.valueOf("-50"));
            }};

            List<Object> values3 = new ArrayList<Object>() { {
                add(1255);
                add(new String("val3"));
                add(Byte.valueOf("50"));
            }};

            List<Object> values4 = new ArrayList<Object>() { {
                add(12555);
                add(new String("val4"));
                add(null);
            }};

            List<Class<?>> valueTypes = new ArrayList<Class<?>>() { {
                add(Integer.class);
                add(String.class);
                add(Byte.class);
            }};

            table = provider.createTable("testTable", valueTypes);
            val1 = provider.createFor(table, values1);
            val2 = provider.createFor(table, values2);
            val3 = provider.createFor(table, values3);
            val4 = provider.createFor(table, values4);
            val5 = provider.createFor(table, values1);

        } catch (ValidityCheckFailedException ex) {
            Assert.fail("validity check failed: " + ex.getMessage());
        } catch (IOException ex) {
            Assert.fail("Input/output error: check failed: " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        provider.removeTable("testTable");
    }

    @Test
    public void getCommitFromAnotherThread() {
        Thread testThread = new Thread() {
            @Override
            public void run() {
                table.put("key2", val1);

                try {
                    table.commit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        };

        testThread.start();
        try {
            testThread.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        Assert.assertEquals("value commited from another thread is not visible", 
            provider.serialize(table, val1), provider.serialize(table, table.get("key2")));
    }

    @Test
    public void sizeCommitFromAnotherThread() {
        int tableSize = 0;

        final Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val1);

                try {
                    table.commit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        };

        Thread testThread2 = new Thread() {
            @Override
            public void run() {
                try {
                    testThread1.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex.getMessage());
                }

                table.put("key2", val2);
                table.put("key1", val3);

                Assert.assertEquals("size incorrect after commit in another thread", table.size(), 2);
            }
        };

        testThread1.start();
        testThread2.start();

        try {
            testThread1.join();
            testThread2.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Test
    public void diffsShouldBeLocal() {

        final Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val2);
                table.put("key2", val2);
                table.rollback();
            }
        };

        Thread testThread2 = new Thread() {
            @Override
            public void run() {
                try {
                    testThread1.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex.getMessage());
                }

                Assert.assertNull("local change reflected in another thread", table.get("key1"));
                Assert.assertNull("local change reflected in another thread", table.get("key2"));
            }
        };

        testThread1.start();
        testThread2.start();

        try {
            testThread1.join();
            testThread2.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Test
    public void concurrentPutCommit() {

        System.out.println("concurrentPutCommit");

        table.put("key1", val1);
        try {
            table.commit();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        final Thread testThread2 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val2);
                try {
                    table.commit(); 
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        };

        final Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val1);

                try {
                    testThread2.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex.getMessage());
                }

                try {
                    Assert.assertEquals("local change overshadowed", 1, table.commit());
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        };

        //testThread1.start();
        testThread2.start();
        try {
           testThread2.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Test
    public void concurrentRemoveRollback() {

        table.put("key1", val2);
        try {
            table.commit();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        final Thread testThread2 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val5);
                try {
                    table.commit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        };

        final Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val1);

                try {
                    testThread2.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex.getMessage());
                }

                Assert.assertEquals("local change doubled from another thread", 0, table.rollback());
            }
        };

        testThread1.start();
        testThread2.start();

        try {
            testThread1.join();
            testThread2.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
