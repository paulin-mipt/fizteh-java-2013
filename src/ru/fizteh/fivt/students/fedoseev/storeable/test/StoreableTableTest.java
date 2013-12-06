package ru.fizteh.fivt.students.fedoseev.storeable.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTable;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableTest {
    private static StoreableTableProvider tp;
    private static File dbDir;
    private static List<Class<?>> types;
    StoreableTable curTable;

    public StoreableTableTest() throws IOException {
        dbDir = new File("world");

        dbDir.mkdirs();

        types = new ArrayList<>();
        tp = new StoreableTableProviderFactory().create("world");

        types.add(Boolean.class);
        types.add(String.class);
    }

    @Before
    public void setUp() throws Exception {
        curTable = tp.createTable("hello", types);
    }

    @After
    public void tearDown() throws Exception {
        tp.removeTable("hello");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        dbDir.delete();
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("hello", curTable.getName());
    }

    @Test
    public void testPutValue() throws Exception {
        Assert.assertNull(curTable.put("1", tp.deserialize(curTable,
                "<row><col>true</col><col>lemon tree</col></row>")));
    }

    @Test
    public void testGetValue() throws Exception {
        curTable.put("2", tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>"));

        Assert.assertEquals("<row><col>true</col><col>lemon tree</col></row>",
                tp.serialize(curTable, curTable.get("2")));
    }

    @Test
    public void testRemove() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");

        curTable.put("3", a);
        Assert.assertEquals(a, curTable.remove("3"));
    }

    @Test
    public void testGet() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");

        curTable.put("4", a);
        Assert.assertEquals(a, curTable.get("4"));

        Storeable b = tp.deserialize(curTable, "<row><col>false</col><col>grape tree</col></row>");

        curTable.put("Россия", b);
        Assert.assertEquals(b, curTable.get("Россия"));
    }

    @Test
    public void testCommit() throws Exception {
        Assert.assertEquals(0, curTable.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(0, curTable.rollback());
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, curTable.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        curTable.put("5", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        curTable.put(null, tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        curTable.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        curTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutInvalidKey() throws Exception {
        curTable.put("   \t  \n", tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>"));
    }

    @Test(expected = ParseException.class)
    public void testPutInvalidValue() throws Exception {
        curTable.put("7", tp.deserialize(curTable, "bububu"));
    }

    @Test(expected = ParseException.class)
    public void testPutValueInvalidTypes() throws Exception {
        curTable.put("8", tp.deserialize(curTable, "<row><col>true</col></row>"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInvalidKey() throws Exception {
        curTable.get("   \t  \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveInvalidKey() throws Exception {
        curTable.remove("   \t  \n");
    }

    @Test
    public void testPutOverwrite() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");

        curTable.put("9", a);
        Assert.assertEquals(a, curTable.put("9", tp.deserialize(curTable,
                "<row><col>true</col><col>lemon tree</col></row>")));
    }

    @Test
    public void testRemoveNotExistingKey() throws Exception {
        Assert.assertNull(curTable.remove("foo"));
    }

    @Test
    public void testGetNotExistingKey() throws Exception {
        Assert.assertNull(curTable.get("void"));
    }


    @Test
    public void testPutOverwriteGet() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");
        Storeable b = tp.deserialize(curTable, "<row><col>false</col><col>grape tree</col></row>");

        curTable.put("10", a);
        curTable.put("10", b);
        Assert.assertEquals(b, curTable.get("10"));
    }

    @Test
    public void testPutRemoveGet() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");
        Storeable b = tp.deserialize(curTable, "<row><col>false</col><col>grape tree</col></row>");

        curTable.put("11", a);
        curTable.put("12", b);
        Assert.assertEquals(a, curTable.get("11"));
        Assert.assertEquals(b, curTable.get("12"));

        curTable.remove("12");
        Assert.assertEquals(a, curTable.get("11"));
        Assert.assertNull(curTable.get("12"));
    }

    @Test
    public void testPutRollbackGet() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>\"lemon tree\"</col></row>");

        curTable.put("13", a);
        curTable.rollback();
        Assert.assertNull(curTable.get("13"));
    }

    @Test
    public void testPutCommitGet() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");

        curTable.put("14", a);
        Assert.assertEquals(1, curTable.commit());
        Assert.assertEquals(a, curTable.get("14"));
    }

    @Test
    public void testPutCommitRemoveRollbackGet() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");

        curTable.put("15", a);
        curTable.commit();
        curTable.remove("15");
        curTable.rollback();
        Assert.assertEquals(a, curTable.get("15"));
    }

    @Test
    public void testPutRemoveSize() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");
        Storeable b = tp.deserialize(curTable, "<row><col>false</col><col>grape tree</col></row>");

        curTable.put("16", a);
        Assert.assertEquals(1, curTable.size());

        curTable.put("17", b);
        Assert.assertEquals(2, curTable.size());

        curTable.remove("18");
        Assert.assertEquals(2, curTable.size());

        curTable.remove("17");
        Assert.assertEquals(1, curTable.size());
    }

    @Test
    public void testPutCommitRollbackSize() throws Exception {
        Storeable a = tp.deserialize(curTable, "<row><col>true</col><col>lemon tree</col></row>");
        Storeable b = tp.deserialize(curTable, "<row><col>false</col><col>grape tree</col></row>");

        curTable.put("19", a);
        Assert.assertEquals(1, curTable.size());

        curTable.put("20", b);
        Assert.assertEquals(2, curTable.size());

        curTable.put("20", b);
        Assert.assertEquals(2, curTable.commit());
        Assert.assertEquals(2, curTable.size());

        curTable.remove("20");
        Assert.assertEquals(1, curTable.size());

        curTable.remove("19");
        Assert.assertEquals(0, curTable.size());
        Assert.assertEquals(2, curTable.rollback());
        Assert.assertEquals(2, curTable.size());
    }
}
