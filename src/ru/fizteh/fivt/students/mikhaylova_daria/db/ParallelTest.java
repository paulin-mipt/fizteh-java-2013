package ru.fizteh.fivt.students.mikhaylova_daria.db;


import org.junit.*;

import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.*;

public class ParallelTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public Hashtable<String, String> actualTable = new Hashtable<>();

    private File mainDir;
    private File tableFile;

    private ru.fizteh.fivt.storage.structured.TableProviderFactory factory;
    private ArrayList<Class<?>> goodTypeList;
    private TableProvider provider;
    private Table table;
    private File goodTableSign;
    private final String goodStrVal
            = "<row><col>12</col></row>";


    @Before
    public void before() {
        factory = new TableManagerFactory();
        goodTypeList = new ArrayList<>();
        goodTypeList.add(Integer.class);
        try {
            mainDir = folder.newFolder("mainDir");
            tableFile = new File(mainDir, "goodTable");
            if (!tableFile.mkdir()) {
                throw new IOException("Creating file error");
            }
            goodTableSign = new File(tableFile, "signature.tsv");
            if (!goodTableSign.createNewFile()) {
                throw new IOException("Creating file error");
            }
            String str = "int";
            try (BufferedWriter signatureWriter =
                         new BufferedWriter(new FileWriter(goodTableSign))) {
                signatureWriter.write(str);
            } catch (IOException e) {
                throw new IOException("Reading error: signature.tsv", e);
            }
            provider = factory.create(mainDir.toString());
            table = provider.getTable("goodTable");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    @After
    public void after() {
        folder.delete();
    }

    private String makeValueString(Integer value) {
        if (value == null) {
            return "<row><nul/></row>";
        }
        return "<row><col>" + value.toString() + "</col></row>";
    }

    @Test
    public void creatingOfTable() throws InterruptedException, ExecutionException {
        ExecutorService serviceFirst = Executors.newSingleThreadExecutor();
        ExecutorService serviceSecond = Executors.newSingleThreadExecutor();
        ExecutorService serviceThread = Executors.newSingleThreadExecutor();
        Callable<Table> createTable = new Callable<Table>() {
            @Override
            public Table call() throws IOException {
                return provider.createTable("newTable", goodTypeList);
            }
        };

        Future<Table> firstFuture = serviceFirst.submit(createTable);
        Future<Table> secondFuture = serviceSecond.submit(createTable);
        if (firstFuture.get() != null && secondFuture.get() != null) {
            fail("Неправильная работа createTable: два потока вернули ненулевые ссылки");
        }
        Future<Table> threadFuture = serviceThread.submit(createTable);
        assertNull("Третий поток на создание уже созданной таблицы не вернул null", threadFuture.get());
        Callable<Table> getCreatedTable = new Callable<Table>() {
            @Override
            public Table call() throws IOException {
                return provider.getTable("newTable");
            }
        };
        Future<Table> getCreatedFuture = serviceSecond.submit(getCreatedTable);
        if (getCreatedFuture.get() == null
                || (getCreatedFuture.get() != firstFuture.get() && getCreatedFuture.get() != secondFuture.get())) {
            fail("Таблица не вернула ссылку на свежесозданную другим потоком таблицу");
        }
        serviceFirst.shutdown();
        if (!serviceFirst.awaitTermination(60, TimeUnit.SECONDS)) {
            serviceFirst.shutdownNow();
        }
        if (!serviceFirst.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("pool did not terminate");
            System.exit(1);
        }
        serviceSecond.shutdown();
        if (!serviceSecond.awaitTermination(60, TimeUnit.SECONDS)) {
            serviceSecond.shutdownNow();
        }
        if (!serviceSecond.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("pool did not terminate");
            System.exit(1);
        }
        serviceThread.shutdown();
        if (!serviceThread.awaitTermination(60, TimeUnit.SECONDS)) {
            serviceThread.shutdownNow();
        }
        if (!serviceThread.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("pool did not terminate");
            System.exit(1);
        }
    }


    @Test
    public void commitsAndRollback() throws InterruptedException, ExecutionException {
        actualTable.clear();
        ExecutorService serviceFirst = Executors.newSingleThreadExecutor();
        ExecutorService serviceSecond = Executors.newSingleThreadExecutor();
        Callable<Integer> emptyCommit = new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                return table.commit();
            }
        };
        Callable<Integer> putsOneTwoThreeFourCommit = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                table.put("key1", provider.deserialize(table, makeValueString(1)));
                table.put("key2", provider.deserialize(table, makeValueString(2)));
                table.put("key3", provider.deserialize(table, makeValueString(3)));
                table.put("key4", provider.deserialize(table, makeValueString(4)));
                int answer = table.commit();
                actualTable.put("key1", makeValueString(1));
                actualTable.put("key2", makeValueString(2));
                actualTable.put("key3", makeValueString(3));
                actualTable.put("key4", makeValueString(4));
                return answer;
            }
        };

        Callable<Integer> putFiveAndDelete1WithoutCommit = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                table.put("key5", provider.deserialize(table, makeValueString(5)));
                return 0;
            }
        };

        Callable<Integer> deleteOneTwoCommit = new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                table.remove("key1");
                table.remove("key2");
                int answer = table.commit();
                actualTable.remove("key1");
                actualTable.remove("key2");
                return answer;
            }
        };

        Callable<Integer> rollback = new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                return table.rollback();
            }
        };
        Future<Integer> firstFutureFirst = serviceFirst.submit(putsOneTwoThreeFourCommit);
        assertEquals("Commit возвращает неправильное значение", firstFutureFirst.get(), Integer.valueOf(4));
        assertEquals(provider.serialize(table, table.get("key1")), actualTable.get("key1"));
        assertEquals(provider.serialize(table, table.get("key2")), actualTable.get("key2"));
        assertEquals(provider.serialize(table, table.get("key3")), actualTable.get("key3"));
        assertEquals(provider.serialize(table, table.get("key4")), actualTable.get("key4"));
        Future<Integer> emptyCommitFutureSecond = serviceSecond.submit(emptyCommit);
        assertEquals("Commit возвращает неправильное значение", emptyCommitFutureSecond.get(), Integer.valueOf(0));
        assertEquals(provider.serialize(table, table.get("key1")), actualTable.get("key1"));
        assertEquals(provider.serialize(table, table.get("key2")), actualTable.get("key2"));
        assertEquals(provider.serialize(table, table.get("key3")), actualTable.get("key3"));
        assertEquals(provider.serialize(table, table.get("key4")), actualTable.get("key4"));
        Future<Integer> putFiveAndDelete1WithoutCommitFuture = serviceSecond.submit(putFiveAndDelete1WithoutCommit);
        assertEquals(provider.serialize(table, table.get("key1")), actualTable.get("key1"));
        assertEquals(provider.serialize(table, table.get("key2")), actualTable.get("key2"));
        assertEquals(provider.serialize(table, table.get("key3")), actualTable.get("key3"));
        assertEquals(provider.serialize(table, table.get("key4")), actualTable.get("key4"));
        assertNull(table.get("key5"));
        Future<Integer> deleteOneTwoCommitFuture = serviceFirst.submit(deleteOneTwoCommit);
        deleteOneTwoCommitFuture.get();
        Callable<Integer> checkStateOfSecond = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                assertNull(table.get("key1"));
                assertNull(table.get("key2"));
                assertEquals(provider.serialize(table, table.get("key3")), actualTable.get("key3"));
                assertEquals(provider.serialize(table, table.get("key4")), actualTable.get("key4"));
                assertNotNull(table.get("key5"));
                return 0;
            }
        };
        Future checkStateOfSecondFuture = serviceSecond.submit(checkStateOfSecond);
        assertEquals(checkStateOfSecondFuture.get(), 0);
        serviceFirst.shutdown();
        if (!serviceFirst.awaitTermination(60, TimeUnit.SECONDS)) {
            serviceFirst.shutdownNow();
        }
        if (!serviceFirst.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("pool did not terminate");
            System.exit(1);
        }
        serviceSecond.shutdown();
        if (!serviceSecond.awaitTermination(60, TimeUnit.SECONDS)) {
            serviceSecond.shutdownNow();
        }
        if (!serviceSecond.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("pool did not terminate");
            System.exit(1);
        }
    }
}
