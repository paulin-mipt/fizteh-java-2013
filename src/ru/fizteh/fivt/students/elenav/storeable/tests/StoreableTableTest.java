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

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.elenav.utils.Functions;

public class StoreableTableTest {
    
    private static final File TABLE_PATH = new File("D:/TableTest");

    private static TableProvider provider;
    private static Table testTable;

    @BeforeClass
    public static void initDir() {
        TABLE_PATH.mkdir();
    }

    @AfterClass
    public static void clearDir() {
        try {
            Functions.deleteRecursively(TABLE_PATH);
        } catch (IOException e) {
            // do nothing
        }
    }

    @Before
    public void init() throws Exception {
        List<Class<?>> columns = new ArrayList<>();
        columns.add(Integer.class);
        columns.add(String.class);
        provider = new StoreableTableProvider(TABLE_PATH, System.err);
        testTable = provider.createTable("first", columns);
    }

    @After
    public void clear() throws Exception {
        provider.removeTable("first");
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(testTable.getName(), "first");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        testTable.get(null);
    }

    @Test
    public void testGet() throws Exception {
        testTable.put("key", provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
        Storeable st = testTable.get("key");
        Assert.assertNotNull(st);
        Assert.assertEquals(st.getIntAt(0), Integer.valueOf(5));
        Assert.assertNull(testTable.get("anotherkey"));
    }
    
    @Test
    public void testCommit() throws Exception {
        testTable.put("1", provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
        testTable.put("3", provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
        testTable.put("5", provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
        Assert.assertEquals(3, testTable.commit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        testTable.put(null, provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        testTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAlienStorable() throws Exception {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Table otherTable = provider.createTable("otherTable", types);
        testTable.put("alienstorable", provider.deserialize(otherTable, "<row><col>5</col></row>"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutWithSpaces() throws Exception {
        testTable.put("   ", provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
    }

    @Test
    public void testPut() throws Exception {
        Storeable st = provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>");
        Assert.assertNull(testTable.put("newKey", st));
        Assert.assertEquals(testTable.put("newKey", provider.deserialize(testTable,
                "<row><col>5</col><col>testval</col></row>")).toString(), st.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        testTable.remove(null);
    }

    @Test
    public void testRemove() throws Exception {
        testTable.put("1", provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
        Assert.assertNull(testTable.remove("not_existing_key"));
        Assert.assertNull(testTable.remove("not_existing_key2"));
        Assert.assertEquals(testTable.remove("1").toString(), "5 value");
    }

    @Test
    public void testGetColumnsCount() throws Exception {
        Assert.assertEquals(testTable.getColumnsCount(), 2);
    }

    @Test
    public void testGetColumnType() throws Exception {
        Assert.assertEquals(testTable.getColumnType(0).getSimpleName(), "Integer");
        Assert.assertEquals(testTable.getColumnType(1).getSimpleName(), "String");
    }
    
    @Test
    public void testSize() throws Exception {
        for (int i = 0; i < 7; ++i) {
            testTable.put("sizeKey" + i, provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
        }
        Assert.assertEquals(7, testTable.size());
        testTable.remove("sizeKey5");
        Assert.assertEquals(6, testTable.size());
        testTable.rollback();
        Assert.assertEquals(0, testTable.size());
    }

    @Test
    public void testRollback() throws Exception {
        for (int i = 0; i < 7; ++i) {
            testTable.put("rollbackKey" + i, 
                    provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>"));
        }
        testTable.commit();
        testTable.put("rollbackKey4", provider.deserialize(testTable, "<row><col>5</col><col>value222</col></row>"));
        testTable.put("newKey", provider.deserialize(testTable, "<row><col>6</col><col>value</col></row>"));
        Assert.assertEquals(2, testTable.rollback());
        Assert.assertNull(testTable.get("newKey"));
        Assert.assertEquals(testTable.get("rollbackKey4").toString(),
                provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>").toString());
    }
    
    @Test
    public void testCommitRollback() throws ColumnFormatException, ParseException, IOException {
        Storeable st = provider.deserialize(testTable, "<row><col>5</col><col>value</col></row>");
        testTable.put("Key1", st);
        testTable.commit();
        testTable.rollback();
        Assert.assertEquals(st, testTable.get("Key1"));
        Assert.assertEquals(st, testTable.put("Key1", st));
    }
    
}
