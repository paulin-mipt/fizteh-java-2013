package ru.fizteh.fivt.students.belousova.storable.tests;

import junit.framework.Assert;
import org.junit.*;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.belousova.storable.StorableTableProviderFactory;
import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StorableTableProviderTest {
    private static TableProvider tableProvider;
    private static List<Class<?>> goodList = new ArrayList<>();
    private static List<Class<?>> badList = new ArrayList<>();
    private static List<Class<?>> emptyList = new ArrayList<>();
    private static Table table;
    private static String testString;

    @BeforeClass
    public static void setUpClass() throws Exception {
        TableProviderFactory tableProviderFactory = new StorableTableProviderFactory();
        tableProvider = tableProviderFactory.create("javatest");
        goodList.add(Integer.class);
        goodList.add(Byte.class);
        goodList.add(Long.class);
        goodList.add(Float.class);
        goodList.add(Double.class);
        goodList.add(String.class);
        goodList.add(Boolean.class);
        testString = "<row><col>5</col><col>0</col><col>65777</col><col>" +
                "5.5</col><col>767.576</col><col>frgedr</col><col>true</col></row>";
        badList.add(null);
        badList.add(IllegalArgumentException.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        File file = new File("javatest");
        if (file.exists()) {
            FileUtils.deleteDirectory(file);
        }
    }

    @Before
    public void setUp() throws Exception {
        table = tableProvider.createTable("table", goodList);
    }

    @After
    public void tearDown() throws Exception {
        tableProvider.removeTable("table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullName() throws Exception {
        tableProvider.createTable(null, goodList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmptyName() throws Exception {
        tableProvider.createTable("", goodList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIncorrectName() throws Exception {
        tableProvider.createTable("@#$@%", goodList);
    }

    @Test
    public void testCreateTableExisted() throws Exception {
        tableProvider.createTable("testCreateTableExisted", goodList);
        Assert.assertNull(tableProvider.createTable("testCreateTableExisted", goodList));
        tableProvider.removeTable("testCreateTableExisted");
    }

    @Test
    public void testCreateTableNotExisted() throws Exception {
        Assert.assertNotNull(tableProvider.createTable("testCreateTableNotExisted", goodList));
        tableProvider.removeTable("testCreateTableNotExisted");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullTypeList() throws Exception {
        tableProvider.createTable("testCreateTableNullTypeList", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmptyTypeList() throws Exception {
        tableProvider.createTable("testCreateTableEmptyTypeList", emptyList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIncorrectTypeList() throws Exception {
        tableProvider.createTable("testCreateTableIncorrectTypeList", badList);
    }

    @Test(expected = ParseException.class)
    public void testDeserializeWrongFormat() throws Exception {
        tableProvider.deserialize(table, "tfoxhncvgixf");
    }

    @Test
    public void testDeserializeRightFormat() throws Exception {
        Assert.assertNotNull(tableProvider.deserialize(table, testString));
    }

    @Test
    public void testSerialize() throws Exception {
        Storeable storeable = tableProvider.deserialize(table, testString);
        Assert.assertEquals(testString, tableProvider.serialize(table, storeable));
    }

    @Test
    public void testCreateForTable() throws Exception {
        Assert.assertNotNull(tableProvider.createFor(table));
    }

    @Test
    public void testCreateForTableAndGoodValues() throws Exception {
        List<Object> values = new ArrayList<>();
        values.add(5);
        values.add(Byte.valueOf((byte) 0));
        values.add(Long.valueOf(47456));
        values.add(Float.valueOf((float) 5.5));
        values.add(5.5);
        values.add("jhfkflg");
        values.add(true);
        Assert.assertNotNull(tableProvider.createFor(table, values));
    }

    @Test(expected = ColumnFormatException.class)
    public void testCreateForTableAndBadValuesColumnFormat() throws Exception {
        List<Object> values = new ArrayList<>();
        values.add(5);
        values.add(0);
        values.add("fkjbdflg");
        values.add(5.5);
        values.add(5.5);
        values.add("jhfkflg");
        values.add(true);
        tableProvider.createFor(table, values);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCreateForTableAndBadValuesOutOfBounds() throws Exception {
        List<Object> values = new ArrayList<>();
        values.add(5);
        values.add(0);
        values.add(47456);
        values.add(5.5);
        values.add(3758.9875);
        values.add("jhfkflg");
        values.add(true);
        values.add("blabla");
        tableProvider.createFor(table, values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        tableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() throws Exception {
        tableProvider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableIncorrect() throws Exception {
        tableProvider.getTable("@#$@%");
    }

    @Test
    public void testGetTableExisted() throws Exception {
        Table table1 = tableProvider.createTable("testGetTableExisted", goodList);
        Assert.assertEquals(table1, tableProvider.getTable("testGetTableExisted"));
    }

    @Test
    public void testGetTableNotExisted() throws Exception {
        Assert.assertNull(tableProvider.getTable("testGetTableNotExisted"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableEmpty() throws Exception {
        tableProvider.removeTable("");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExisted() throws Exception {
        tableProvider.removeTable("testRemoveTableNotExisted");
    }

    @Test
    public void testRemoveTableExisted() throws Exception {
        Table table1 = tableProvider.createTable("testRemoveTableExisted", goodList);
        tableProvider.removeTable("testRemoveTableExisted");
        Assert.assertNull(tableProvider.getTable("testRemoveTableExisted"));
    }
}
