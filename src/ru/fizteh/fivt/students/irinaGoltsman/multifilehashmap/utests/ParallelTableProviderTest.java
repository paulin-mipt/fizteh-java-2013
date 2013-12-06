package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ParallelTableProviderTest {
    private static List<Class<?>> columnTypes = new ArrayList<>();
    private static TableProvider provider;
    boolean flagOfThread1 = true;
    boolean flagOfThread2 = true;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTableProvider() throws IOException, ParseException {
        provider = new DBTableProvider(rootDBDirectory.newFolder());
        columnTypes.add(Integer.class);
    }

    @Test(timeout = 5000)
    public void parallelCreateTables() throws IOException {
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                try {
                    provider.createTable("table1", columnTypes);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                try {
                    provider.createTable("table2", columnTypes);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
        if (provider.createTable("table1", columnTypes) != null) {
            provider.removeTable("table1");
        }
        if (provider.createTable("table2", columnTypes) != null) {
            provider.removeTable("table2");
        }
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            //
        }
        Assert.assertNull(provider.createTable("table1", columnTypes));
        Assert.assertNull(provider.createTable("table2", columnTypes));
        provider.removeTable("table1");
        provider.removeTable("table2");
    }

    @Test(timeout = 5000)
    public void parallelCreateAndGetTable() throws IOException {
        Thread threadCreateTable = new Thread(new Runnable() {
            public void run() {
                try {
                    provider.createTable("newTable", columnTypes);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        Thread threadGetCreatedTable = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (provider.getTable("newTable") != null) {
                        try {
                            provider.removeTable("newTable");
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    }
                    //System.out.println("No table yet");
                }
            }
        });

        threadCreateTable.start();
        threadGetCreatedTable.start();
        try {
            threadCreateTable.join();
            threadGetCreatedTable.join();
        } catch (InterruptedException e) {
            //
        }
    }


    @Test
    public void concurrentCreateSameTable() throws IOException {
        flagOfThread1 = true;
        flagOfThread2 = true;
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    flagOfThread1 = (provider.createTable("tmpTable", columnTypes) == null);
                } catch (IOException e) {
                    throw new RuntimeException("error when creating a table: " + e.getMessage());
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    flagOfThread2 = (provider.createTable("tmpTable", columnTypes) == null);
                } catch (IOException e) {
                    throw new RuntimeException("error when creating a table: " + e.getMessage());
                }
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            //
        }
        provider.removeTable("tmpTable");
        Assert.assertTrue(flagOfThread1 ^ flagOfThread2);
    }

    @Test
    public void concurrentRemoveSameTable() throws IOException {
        flagOfThread1 = true;
        flagOfThread2 = true;
        provider.createTable("tableWhichWillRemoved", columnTypes);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.removeTable("tableWhichWillRemoved");
                } catch (IOException e) {
                    throw new RuntimeException("error when removing a table: " + e.getMessage());
                } catch (IllegalStateException e) {
                    flagOfThread1 = false;
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.removeTable("tableWhichWillRemoved");
                } catch (IOException e) {
                    throw new RuntimeException("error when removing a table: " + e.getMessage());
                } catch (IllegalStateException e) {
                    flagOfThread2 = false;
                }
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            //
        }
        Assert.assertTrue(flagOfThread1 ^ flagOfThread2);
    }
}
