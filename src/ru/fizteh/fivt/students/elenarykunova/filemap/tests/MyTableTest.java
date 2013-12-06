package ru.fizteh.fivt.students.elenarykunova.filemap.tests;

import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.elenarykunova.filemap.*;

public class MyTableTest {

    private static MyTable table;
    private MyTableProvider prov;
    private String tablePath;
    
    @Rule 
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() {
        File rootDir;
        try {
            rootDir = folder.newFolder("myroot");
            MyTableProviderFactory factory = new MyTableProviderFactory();
            prov = (MyTableProvider) factory.create(rootDir.getAbsolutePath());
            List<Class<?>> types = new ArrayList<Class<?>>(4);
            types.add(Integer.class);
            types.add(Double.class);
            types.add(String.class);
            types.add(Boolean.class);
            tablePath = rootDir.getAbsoluteFile() + File.separator + "newTable";
            table = (MyTable) prov.createTable("newTable", types);
        } catch (IOException e) {
            System.err.println("can't make tests");
        }
    }
    
    @Test
    public void testGetName() {
        assertNotNull(table.getName());
        assertEquals(table.getName(), "newTable");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        table.put(null, new MyStoreable(table));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutEmpty() {
        table.put("", new MyStoreable(table));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNl() {
        table.put("                     ", new MyStoreable(table));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNullVal() {
        table.put("alala", null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testGetNull() {
        table.get(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetEmpty() {
        table.get("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testGetNl() {
        table.get("                 ");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        table.remove(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveEmpty() {
        table.remove("");
    }

    @Test
    public void testPutBool() throws IllegalArgumentException, ParseException {
        String valStr1 = "[1,1.5,\"value\", \"true\"]";
        Storeable val1 = prov.deserialize(table, valStr1);
        
        table.put("azaza", val1);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNl() {
        table.remove("              ");
    }

    @Test
    public void testPutGetRemove() throws IllegalArgumentException, ParseException {
        String valStr1 = "[1,1.5,\"value\",false]";
        Storeable val1 = prov.deserialize(table, valStr1);
        assertNull(table.put("key", val1));
        
        String valStr2 = "[1,1.5,\"value2\",false]";
        Storeable val2 = prov.deserialize(table, valStr2);
        assertNotNull(table.put("key", val2));
        
        assertNotEquals(table.get("key"), val1);
        
        assertEquals(table.put("key", val1), val2);
        assertNull(table.get("other_key"));
        
        assertEquals(table.remove("key"), val1);
        
        assertNull(table.get("key"));
    }

    @Test
    public void testPutGetRemoveCyrillic() throws IllegalArgumentException, ParseException {
        String valStr1 = "[1, 1.5, значение1,false]";
        Storeable val1 = prov.deserialize(table, valStr1);
        assertNull(table.put("ключ", val1));
        
        String valStr2 = "[1, 1.5, значение2,false]";
        Storeable val2 = prov.deserialize(table, valStr2);
        assertNotNull(table.put("ключ", val2));
        
        assertNotEquals(table.get("ключ"), val1);
        
        assertEquals(table.put("ключ", val1), val2);
        assertNull(table.get("другой_ключ"));
        
        assertEquals(table.remove("ключ"), val1);
        
        assertNull(table.get("ключ"));
    }

    @Test
    public void testSizeCommitRollback() throws IllegalArgumentException, ParseException {
        int sz = 442;
        for (int i = 0; i < sz; i++) {
            String valStr = "[" + Integer.toString(i + 1) + ", 2.3, value_commit,false]";
            Storeable val = prov.deserialize(table, valStr);
            table.put(Integer.toString(i), val);
        }
        assertEquals(table.size(), sz);
        assertEquals(table.commit(), sz);
        assertEquals(table.size(), sz);
        assertEquals(table.rollback(), 0);
        for (int i = 0; i < sz; i++) {
            table.remove(Integer.toString(i));
        }        
        assertEquals(table.size(), 0);
        assertEquals(table.rollback(), sz);
    }

    @Test
    public void testRollback() throws IllegalArgumentException, ParseException {
        String valStr1 = "[0, 2, val1,false]";
        Storeable val1 = prov.deserialize(table, valStr1);
        table.put("11", val1);
        assertEquals(table.commit(), 1);
        table.remove("11");
        assertEquals(table.rollback(), 1);
        assertEquals(table.size(), 1);
        
        String valStr2 = "[4, 2, val2,false]";
        Storeable val2 = prov.deserialize(table, valStr2);
        table.put("11", val2);
        table.put("11", val1);
        assertEquals(table.rollback(), 0);
        
        table.remove("11");
        assertEquals(table.rollback(), 1);
        
        assertNotNull(table.get("11"));
        
        String valStrInit = "[0, 0.3, a,false]";
        Storeable valInit = prov.deserialize(table, valStrInit);

        table.put("key", valInit);
        table.commit();

        String valStr4 = "[0, 0.3, changed,false]";
        Storeable val4 = prov.deserialize(table, valStr4);

        table.put("key", val4);

        String valStr5 = "[0, 0.3, changed_again,false]";
        Storeable val5 = prov.deserialize(table, valStr5);

        table.put("key", val5);
        table.put("key", valInit);
        assertEquals(table.rollback(), 0);
        
        table.put("newKey", val1);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        assertEquals(table.rollback(), 0);
    }

    @Test
    public void testCommit() throws IllegalArgumentException, ParseException {
        String valStr1 = "[0, 2, val1,false]";
        Storeable val1 = prov.deserialize(table, valStr1);
        table.put("11", val1);
        assertEquals(table.commit(), 1);
        
        String valStr2 = "[4, 2, val2,false]";
        Storeable val2 = prov.deserialize(table, valStr2);
        assertEquals(table.put("11", val2), val1);

        String valStrNew = "[0, 0.3, a,false]";
        Storeable valNew = prov.deserialize(table, valStrNew);

        table.put("k", valNew);
        assertEquals(2, table.commit());

        String valStr4 = "[0, 0.3, changed,false]";
        Storeable val4 = prov.deserialize(table, valStr4);

        table.put("k", val4);

        String valStr5 = "[0, 0.3, changed_again,false]";
        Storeable val5 = prov.deserialize(table, valStr5);

        table.put("k", val5);
        table.put("k", valNew);
        assertEquals(0, table.commit());

        table.remove("11");
        table.remove("k");
        assertEquals(2, table.commit());
        assertEquals(0, table.size());

        table.put("newKey", val1);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        assertEquals(0, table.commit());

    }
    
    @Test (expected = ColumnFormatException.class)
    public void putBigStoreable() throws IllegalArgumentException, RuntimeException, IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>(5);
        columnTypes.add(Integer.class);
        columnTypes.add(Double.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        columnTypes.add(Integer.class);
        MyTable big = (MyTable) prov.createTable("big", columnTypes);
        Storeable bigger = prov.createFor(big);
        table.put("key1", bigger);
    }

    @Test (expected = ColumnFormatException.class)
    public void putSmallStoreable() throws IllegalArgumentException, RuntimeException, IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>(2);
        columnTypes.add(Integer.class);
        columnTypes.add(Double.class);
        MyTable small = (MyTable) prov.createTable("small", columnTypes);
        Storeable smaller = prov.createFor(small);
        table.put("key2", smaller);
    }

    @Test (expected = ColumnFormatException.class)
    public void putBadStoreable() throws IllegalArgumentException, RuntimeException, IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>(3);
        columnTypes.add(0, Boolean.class);
        columnTypes.add(1, Long.class);
        columnTypes.add(2, Float.class);
        List<Object> values = new ArrayList<Object>(3);
        values.add(0, true);
        values.add(1, 1200000000);
        values.add(1.323333333);
        MyTable bad = (MyTable) prov.createTable("bad", columnTypes);
        Storeable badStoreable = prov.createFor(bad, values);
        table.put("key3", badStoreable);
    }
    
    @Test
    public void close() throws Exception {
        table.close();
        table.close();
    }

    @Test (expected = IllegalStateException.class)
    public void closeCommit() throws Exception {
        table.close();
        table.commit();
    }

    @Test (expected = IllegalStateException.class)
    public void closeRollback() throws Exception {
        table.close();
        table.rollback();
    }

    @Test (expected = IllegalStateException.class)
    public void closeGet() throws Exception {
        table.close();
        table.get("1");
    }
    
    @Test (expected = IllegalStateException.class)
    public void closePut() throws Exception {
        table.close();
        MyStoreable stor = (MyStoreable) prov.createFor(table);
        stor.setColumnAt(0, 1);
        table.put("1", stor);
    }
    
    @Test (expected = IllegalStateException.class)
    public void closeSize() throws Exception {
        table.close();
        table.size();
    }

    @Test (expected = IllegalStateException.class)
    public void closeRemove() throws Exception {
        table.close();
        table.remove("1");
    }

    @Test (expected = IllegalStateException.class)
    public void closeColumnsCount() throws Exception {
        table.close();
        table.getColumnsCount();
    }

    @Test (expected = IllegalStateException.class)
    public void closeColumnType() throws Exception {
        table.close();
        table.getColumnType(1);
    }
    
    @Test
    public void toStringTest() {
        assertEquals("MyTable[" + tablePath + "]", table.toString());
    }
}
