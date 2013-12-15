package ru.fizteh.fivt.students.ermolenko.storable.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapUtils;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTable;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTableProvider;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTableProviderFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MyStoreableTest {

    private static StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
    private static StoreableTableProvider tableProvider;
    private static String testString;
    private static String testString1;
    private static File database;
    private Storeable testStorable;
    private static StoreableTable table;

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

    @Test(expected = ColumnFormatException.class)
    public void testSetColumnAtWrongColumnFormat() throws Exception {

        String str = "java";
        testStorable.setColumnAt(0, str);
    }

    @Test
    public void testSetColumnAt() throws Exception {

        String str = "java";
        testStorable.setColumnAt(5, str);
        Assert.assertEquals(str, testStorable.getStringAt(5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnAtOutOfBounds() throws Exception {

        testStorable.getColumnAt(10);
    }

    @Test
    public void testGetColumnAt() throws Exception {

        Assert.assertEquals(5, testStorable.getColumnAt(0));
    }

    @Test
    public void testGetIntAt() throws Exception {

        Assert.assertEquals(Integer.valueOf(5), testStorable.getIntAt(0));
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetIntAtWrongColumnFormat() throws Exception {

        testStorable.getIntAt(1);
    }

    @Test
    public void testGetByteAt() throws Exception {

        Assert.assertEquals(Byte.valueOf("0"), testStorable.getByteAt(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetByteAtOutOfBounds() throws Exception {

        testStorable.getByteAt(10);
    }

    @Test
    public void testGetDoubleAt() throws Exception {

        Assert.assertEquals((Double) 767.576, testStorable.getDoubleAt(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetDoubleAtOutOfBounds() throws Exception {

        testStorable.getDoubleAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetDoubleAtWrongColumnFormat() throws Exception {
        testStorable.getDoubleAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetStringAtOutOfBounds() throws Exception {
        testStorable.getStringAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetStringAtWrongColumnFormat() throws Exception {
        testStorable.getStringAt(1);
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        Assert.assertTrue(testStorable.getBooleanAt(6));
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetBooleanAtWrongColumnFormat() throws Exception {
        testStorable.getStringAt(1);
    }

    @Test
    public void testEqualsTrue() throws Exception {
        Assert.assertTrue(testStorable.equals(testStorable));
    }
}
