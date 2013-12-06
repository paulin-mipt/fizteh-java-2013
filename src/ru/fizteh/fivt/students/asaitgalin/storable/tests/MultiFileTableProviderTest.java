package ru.fizteh.fivt.students.asaitgalin.storable.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.asaitgalin.storable.MultiFileTableProviderFactory;
import ru.fizteh.fivt.students.asaitgalin.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MultiFileTableProviderTest {
    private static final File DB_TEST_PATH = new File("./testsProvider");

    private TableProviderFactory factory = new MultiFileTableProviderFactory();
    private TableProvider provider;
    private Table table;

    @BeforeClass
    public static void globalSetUp() {
        DB_TEST_PATH.mkdir();
    }

    @AfterClass
    public static void globalTearDown() {
        try {
            FileUtils.deleteRecursively(DB_TEST_PATH);
        } catch (IOException ioe) {
            //
        }
    }

    @Before
    public void setUp() throws Exception {
        provider = factory.create(DB_TEST_PATH.getAbsolutePath());
        List<Class<?>> columns = new ArrayList<>();
        columns.add(Integer.class);
        columns.add(String.class);
        table = provider.createTable("storeableTable", columns);
    }

    @After
    public void tearDown() throws Exception {
        provider.removeTable("storeableTable");
    }

    @Test
    public void testCreateTable() throws Exception {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        Assert.assertNotNull(provider.createTable("table1", list));
        Assert.assertNull(provider.createTable("table1", list));
        provider.removeTable("table1");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableBadSymbols() throws Exception {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        provider.createTable(":123+", list);
    }

    @Test(expected = RuntimeException.class)
    public void testGetTableBadSymbols() throws Exception {
        provider.getTable(":123+");
    }

    @Test
    public void testGetTableInstance() throws Exception {
        List<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        Table table = provider.createTable("newtable", list);
        Assert.assertSame(provider.getTable("newtable"), table);
        Assert.assertSame(provider.getTable("newtable"), provider.getTable("newtable"));
        provider.removeTable("newtable");
    }

    @Test
    public void testGetTable() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        try {
            provider.createTable("table2", list);
            Assert.assertNotNull(provider.getTable("table2"));
            provider.removeTable("table2");
        } catch (IOException ioe) {
            //
        }

    }

    @Test
    public void testGetNonExistingTable() {
        Assert.assertNull(provider.getTable("unknownTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithNull() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNull() throws Exception {
        provider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithNull() throws Exception {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
    }

    @Test
    public void testCreateFor() throws Exception {
        Storeable storeable = provider.createFor(table);
        Assert.assertNotNull(storeable);
        storeable.setColumnAt(0, 54);
        Assert.assertNotNull(storeable.getIntAt(0));
    }

    @Test
    public void testCreateForWithList() throws Exception {
        List<Object> values = new ArrayList<>();
        values.add(5);
        values.add("value");
        Storeable storeable = provider.createFor(table, values);
        Assert.assertNotNull(storeable);
        Assert.assertNotNull(storeable.getIntAt(0));
        Assert.assertEquals(storeable.getIntAt(0), Integer.valueOf(5));
    }

    @Test(expected = ParseException.class)
    public void testDeserializeError() throws Exception {
        provider.deserialize(table, "noxmlcontainingstring");
    }

    @Test
    public void testDeserialize() throws Exception {
        Storeable st = provider.deserialize(table, "<row><col>5</col><col>value</col></row>");
        Assert.assertEquals(st.getIntAt(0), Integer.valueOf(5));
        Assert.assertEquals(st.getStringAt(1), "value");
    }

    @Test
    public void testSerialize() throws Exception {
        Storeable st = provider.createFor(table);
        st.setColumnAt(0, 5);
        st.setColumnAt(1, "value");
        String serialized = provider.serialize(table, st);
        Assert.assertEquals(serialized, "<row><col>5</col><col>value</col></row>");
    }
}
