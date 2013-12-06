package ru.fizteh.fivt.students.elenav.storeable.tests;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableProviderFactory;
import ru.fizteh.fivt.students.elenav.utils.Functions;

public class StoreableTableProviderTest {

    private static final File TEST_DIR = new File("D:/ProviderTests");

    private TableProviderFactory factory = new StoreableTableProviderFactory();
    private TableProvider provider;
    private Table table;

    @BeforeClass
    public static void initProvider() {
        TEST_DIR.mkdir();
    }

    @AfterClass
    public static void clearProvider() {
        try {
            Functions.deleteRecursively(TEST_DIR);
        } catch (IOException e) {
            // do nothing
        }
    }

    @Before
    public void init() throws Exception {
        provider = factory.create(TEST_DIR.getAbsolutePath());
        List<Class<?>> columns = new ArrayList<>();
        columns.add(Integer.class);
        columns.add(String.class);
        table = provider.createTable("mystoreable", columns);
    }

    @After
    public void clear() throws Exception {
        provider.removeTable("mystoreable");
    }

    @Test
    public void testCreateTable() throws Exception {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        Assert.assertNotNull(provider.createTable("first", list));
        Assert.assertNull(provider.createTable("first", list));
        provider.removeTable("first");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableInvalidName() throws Exception {
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        provider.createTable("::be  be?be", list);
    }

    @Test(expected = RuntimeException.class)
    public void testGetTableInvalidName() throws Exception {
        provider.getTable("::be  be?be");
    }

    @Test
    public void testGetTableSame() throws Exception {
        List<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        Table table = provider.createTable("first", list);
        Assert.assertSame(provider.getTable("first"), provider.getTable("first"));
        Assert.assertSame(provider.getTable("first"), table);
        provider.removeTable("first");
    }

    @Test
    public void testGetTable() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Double.class);
        try {
            provider.createTable("first", list);
            Assert.assertNotNull(provider.getTable("first"));
            provider.removeTable("first");
        } catch (IOException e) {
            // do nothing
        }
    }

    @Test
    public void testGetTableThatNotExists() {
        Assert.assertNull(provider.getTable("first"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
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
    public void testRemoveTableThatNotExists() throws Exception {
        provider.removeTable("first");
    }

    @Test
    public void testCreateFor() throws Exception {
        Storeable storeable = provider.createFor(table);
        Assert.assertNotNull(storeable);
        storeable.setColumnAt(0, 666);
        Assert.assertNotNull(storeable.getIntAt(0));
    }

    @Test
    public void testCreateForWithList() throws Exception {
        List<Object> values = new ArrayList<>();
        values.add(6);
        values.add("6value");
        Storeable storeable = provider.createFor(table, values);
        Assert.assertNotNull(storeable);
        Assert.assertNotNull(storeable.getIntAt(0));
        Assert.assertEquals(storeable.getIntAt(0), Integer.valueOf(6));
    }

    @Test(expected = ParseException.class)
    public void testDeserializeError() throws Exception {
        provider.deserialize(table, "wrongInput");
    }

    @Test
    public void testDeserialize() throws Exception {
        Storeable st = provider.deserialize(table, "<row><col>6</col><col>6value</col></row>");
        Assert.assertEquals(st.getIntAt(0), Integer.valueOf(6));
        Assert.assertEquals(st.getStringAt(1), "6value");
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
