package ru.fizteh.fivt.students.belousova.storable.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.belousova.storable.ChangesCountingTable;
import ru.fizteh.fivt.students.belousova.storable.StorableTableProvider;
import ru.fizteh.fivt.students.belousova.storable.StorableTableProviderFactory;
import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorableTableLineTest {
    private static StorableTableProviderFactory tableProviderFactory = new StorableTableProviderFactory();
    private static StorableTableProvider tableProvider;
    private static String testString;
    private static String testString1;
    private Storeable testStorable;
    private static ChangesCountingTable table;

    @BeforeClass
    public static void setUpClass() throws Exception {
        tableProvider = tableProviderFactory.create("javatest");
        testString = "<row><col>5</col><col>0</col><col>65777</col><col>" +
                "5.5</col><col>767.576</col><col>frgedr</col><col>true</col></row>";
        testString1 = "<row><col>5</col><col>0</col><col>65777</col><col>" +
                "5.5</col><col>767.576</col><col>frgedr</col><col>true</col></row>";
    }

    @Before
    public void setUp() throws Exception {
        if (tableProvider.getTable("testTable") != null) {
            tableProvider.removeTable("testTable");
        }
        List<Class<?>> goodList = new ArrayList<>();
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
        if (tableProvider.getTable("testTable") != null) {
            tableProvider.removeTable("testTable");
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        File file = new File("javatest");
        if (file.exists()) {
            FileUtils.deleteDirectory(file);
        }
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetColumnAtWrongColumnFormat() throws Exception {
        String str = "java";
        testStorable.setColumnAt(0, str);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetColumnAtOutOfBounds() throws Exception {
        String str = "java";
        testStorable.setColumnAt(10, str);
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

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntAtOutOfBounds() throws Exception {
        testStorable.getIntAt(10);
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

    @Test(expected = ColumnFormatException.class)
    public void testGetByteAtWrongColumnFormat() throws Exception {
        testStorable.getByteAt(2);
    }

    @Test
    public void testGetLongAt() throws Exception {
        Assert.assertEquals(Long.valueOf(65777), testStorable.getLongAt(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetLongAtOutOfBounds() throws Exception {
        testStorable.getLongAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetLongAtWrongColumnFormat() throws Exception {
        testStorable.getLongAt(1);
    }

    @Test
    public void testGetFloatAt() throws Exception {
        float f = (float) 5.5;
        Assert.assertEquals(Float.valueOf(f), testStorable.getFloatAt(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetFloatAtOutOfBounds() throws Exception {
        testStorable.getFloatAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetFloatAtWrongColumnFormat() throws Exception {
        testStorable.getFloatAt(1);
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

    @Test
    public void testGetStringAt() throws Exception {
        Assert.assertEquals("frgedr", testStorable.getStringAt(5));
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

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBooleanAtOutOfBounds() throws Exception {
        testStorable.getStringAt(10);
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
