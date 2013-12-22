package ru.fizteh.fivt.students.ermolenko.storable.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapUtils;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTable;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTableProvider;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableTest {

    private static StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
    private static StoreableTableProvider tableProvider;
    private static String testString;
    private static String testString1;
    private static Storeable testStorable;
    private static File database;
    private StoreableTable table;

    @BeforeClass
    public static void setUpClass() throws Exception {

        database = new File("javatest").getCanonicalFile();
        database.mkdir();
        tableProvider = tableProviderFactory.create("javatest");
        testString = "<row><col>5</col><col>0</col><col>65777</col><col>"
                + "5.5</col><col>767.576</col><col>frgedr</col><col>true</col></row>";
        testString1 = "<row><col>5</col><col>0</col><col>65777</col><col>"
                + "5.5</col><col>767.576</col><col>frgedr</col><col>true</col></row>";
    }

    @Before
    public void setUp() throws Exception {

        if (tableProvider.getTable("testTable") != null) {
            tableProvider.removeTable("testTable");
        }
        List<Class<?>> goodList = new ArrayList<Class<?>>();
        goodList.add(Integer.class);
        goodList.add(Byte.class);
        goodList.add(Long.class);
        goodList.add(Float.class);
        goodList.add(Double.class);
        goodList.add(String.class);
        goodList.add(Boolean.class);
        table = tableProvider.createTable("testTable", goodList);
        testStorable = tableProvider.deserialize(table, testString);
    }

    @After
    public void tearDown() throws Exception {

        tableProvider.removeTable("testTable");
        File file = new File(String.valueOf(Paths.get("javatest", table.getName())));
        MultiFileHashMapUtils.deleteDirectory(file);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {

        File file = new File("javatest");
        if (file.exists()) {
            MultiFileHashMapUtils.deleteDirectory(file);
        }
    }

    @Test
    public void testGetName() throws Exception {

        Assert.assertEquals(table.getName(), "testTable");
    }

    @Test
    public void testGetEnglish() throws Exception {

        table.put("testGetEnglishKey", testStorable);
        Assert.assertEquals(table.get("testGetEnglishKey"), testStorable);
    }

    @Test
    public void testGetRussian() throws Exception {

        table.put("ключ", testStorable);
        Assert.assertEquals(table.get("ключ"), testStorable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {

        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() throws Exception {

        table.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNl() throws Exception {

        table.get("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetKeyWithWhitespaces() throws Exception {

        table.get("one two three");
    }

    @Test
    public void testPutNew() throws Exception {

        Assert.assertNull(table.put("testPutNewKey", testStorable));
        table.remove("testPutNewKey");
    }

    @Test
    public void testPutOld() throws Exception {

        Storeable storeable = tableProvider.deserialize(table, testString1);
        table.put("testPutOldKey", testStorable);
        Assert.assertEquals(table.put("testPutOldKey", storeable), testStorable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {

        table.put(null, testStorable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws Exception {

        table.put("", testStorable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNlKey() throws Exception {

        table.put("   ", testStorable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutKeyWithWhitespaces() throws Exception {

        table.put("one two three", testStorable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {

        table.put("testPutNullValueKey", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAlienValue() throws Exception {

        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(String.class);
        StoreableTable table1 = tableProvider.createTable("table1", list);
        Storeable storeable = tableProvider.deserialize(table1, "<row><col>jtfh</col></row>");
        table.put("testPutNullValueKey", storeable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {

        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() throws Exception {

        table.remove("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveKeyWithWhitespaces() throws Exception {

        table.get("one two three");
    }

    @Test
    public void testRemoveExisted() throws Exception {

        table.put("testRemoveExistedKey", testStorable);
        Assert.assertEquals(table.remove("testRemoveExistedKey"), testStorable);
    }

    @Test
    public void testRemoveNotExisted() throws Exception {

        Assert.assertNull(table.remove("testRemoveNotExistedKey"));
    }

    @Test
    public void testSize() throws Exception {

        table.put("testSizeKey1", testStorable);
        table.put("testSizeKey2", testStorable);
        table.put("testSizeKey3", testStorable);
        Assert.assertEquals(table.size(), 3);
    }

    @Test
    public void testPutRollback() throws Exception {

        table.put("testPutRollbackKey1", testStorable);
        table.put("testPutRollbackKey2", testStorable);
        table.put("testPutRollbackKey3", testStorable);
        Assert.assertEquals(table.rollback(), 3);
    }

    @Test
    public void testPutRemoveRollback() throws Exception {

        table.put("testPutRemoveRollbackKey", testStorable);
        table.remove("testPutRemoveRollbackKey");
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void testRemovePutRollback() throws Exception {

        table.put("testRemovePutRollbackKey", testStorable);
        table.commit();
        table.remove("testPutRemoveRollbackKey");
        table.put("testRemovePutRollbackKey", testStorable);
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void testPutCommit() throws Exception {

        table.put("testPutCommitKey1", testStorable);
        table.put("testPutCommitKey2", testStorable);
        table.put("testPutCommitKey3", testStorable);
        Assert.assertEquals(table.commit(), 3);
    }

    @Test
    public void testPutRemoveCommit() throws Exception {

        table.put("testPutRemoveCommitKey", testStorable);
        table.remove("testPutRemoveCommitKey");
        Assert.assertEquals(table.commit(), 0);
    }

    @Test
    public void testRemovePutCommit() throws Exception {

        table.put("testRemovePutCommitKey", testStorable);
        table.commit();
        table.remove("testPutRemoveCommitKey");
        table.put("testRemovePutCommitKey", testStorable);
        Assert.assertEquals(table.commit(), 0);
    }

    @Test
    public void testGetColumnsCount() throws Exception {

        Assert.assertEquals(7, table.getColumnsCount());
    }

    @Test
    public void testGetColumnType() throws Exception {

        Assert.assertEquals(Integer.class, table.getColumnType(0));
        Assert.assertEquals(Byte.class, table.getColumnType(1));
        Assert.assertEquals(Long.class, table.getColumnType(2));
        Assert.assertEquals(Float.class, table.getColumnType(3));
        Assert.assertEquals(Double.class, table.getColumnType(4));
        Assert.assertEquals(String.class, table.getColumnType(5));
        Assert.assertEquals(Boolean.class, table.getColumnType(6));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnTypeOutOfBounds() throws Exception {

        table.getColumnType(10);
    }

    @Test
    public void testThreadsPutPutCommit() throws Exception {

        Thread firstThread = new Thread(new Runnable() {

            @Override
            public void run() {
                table.put("tryingToPutFirstKey", testStorable);

                try {
                    table.commit();
                } catch (IOException e) {
                    throw new IllegalArgumentException("something going wrong");
                }
            }
        });

        Thread secondThread = new Thread(new Runnable() {

            @Override
            public void run() {
                table.put("tryingToPutSecondKey", testStorable);
                try {
                    table.commit();
                } catch (IOException e) {
                    throw new IllegalArgumentException("something going wrong");
                }
            }
        });

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        Assert.assertEquals(testStorable, table.get("tryingToPutFirstKey"));
        Assert.assertEquals(testStorable, table.get("tryingToPutSecondKey"));
    }
}
