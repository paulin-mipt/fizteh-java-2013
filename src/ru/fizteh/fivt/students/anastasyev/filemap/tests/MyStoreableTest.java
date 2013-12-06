package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;
import ru.fizteh.fivt.students.anastasyev.filemap.MyStoreable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class MyStoreableTest {
    TableProviderFactory factory;
    TableProvider tableProvider;
    Table currTable;
    List<Class<?>> classes;
    List<Object> values;
    Storeable storeableWithValues;
    Storeable clearStoreable;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setStoreable() throws IOException {
        factory = new FileMapTableProviderFactory();
        classes = new ArrayList<Class<?>>();
        classes.add(Integer.class);
        classes.add(Long.class);
        classes.add(Byte.class);
        classes.add(Float.class);
        classes.add(Double.class);
        classes.add(Boolean.class);
        classes.add(String.class);
        classes.add(Integer.class);
        classes.add(String.class);
        tableProvider = factory.create(folder.newFolder().toString());
        assertNotNull(tableProvider);
        currTable = tableProvider.createTable("TestTable", classes);
        values = new ArrayList<Object>();
        values.add((Object) 1);
        values.add((Object) 2);
        values.add((Object) 3);
        values.add((Object) 4.5);
        values.add((Object) 5);
        values.add((Object) true);
        values.add((Object) "string");
        values.add((Object) 7);
        values.add((Object) "another string");
        storeableWithValues = new MyStoreable(currTable, values);
        clearStoreable = new MyStoreable(currTable);
    }

    @Test
    public void testSetColumnAt() throws Exception {
        clearStoreable.setColumnAt(1, Integer.valueOf(5).longValue());
        assertEquals(clearStoreable.getLongAt(1), (Long) Integer.valueOf(5).longValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testOutOfBoundIndexSet() throws Exception {
        clearStoreable.setColumnAt(55, Integer.valueOf(5).longValue());
    }

    @Test(expected = ColumnFormatException.class)
    public void testWrongColumnSet() throws Exception {
        clearStoreable.setColumnAt(0, Integer.valueOf(5).longValue());
    }

    @Test
    public void testGetColumnAt() throws Exception {
        assertEquals(storeableWithValues.getColumnAt(6), (Object) "string");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testOutOfBoundIndexGet() throws Exception {
        storeableWithValues.getColumnAt(55);
    }

    @Test
    public void testGetIntAt() throws Exception {
        assertEquals(storeableWithValues.getIntAt(0), (Object) 1);
        assertEquals(storeableWithValues.getIntAt(7), (Object) 7);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntAtBigIndex() throws Exception {
        storeableWithValues.getIntAt(55);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetIntAtWrongIndex() throws Exception {
        storeableWithValues.getIntAt(2);
    }

    @Test
    public void testGetLongAt() throws Exception {
        assertEquals(storeableWithValues.getLongAt(1), (Long) Integer.valueOf(2).longValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetLongAtBigIndex() throws Exception {
        storeableWithValues.getLongAt(55);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetLongAtWrongIndex() throws Exception {
        storeableWithValues.getLongAt(0);
    }

    @Test
    public void testGetByteAt() throws Exception {
        assertEquals(storeableWithValues.getByteAt(2), (Byte) Integer.valueOf(3).byteValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetByteAtBigIndex() throws Exception {
        storeableWithValues.getByteAt(55);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetByteAtWrongIndex() throws Exception {
        storeableWithValues.getByteAt(1);
    }

    @Test
    public void testGetFloatAt() throws Exception {
        assertEquals(storeableWithValues.getFloatAt(3), ((Double) 4.5).floatValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetFloatAtBigIndex() throws Exception {
        storeableWithValues.getFloatAt(55);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetFloatAtWrongIndex() throws Exception {
        storeableWithValues.getFloatAt(2);
    }

    @Test
    public void testGetDoubleAt() throws Exception {
        assertEquals(storeableWithValues.getDoubleAt(4), (Double) ((Integer) 5).doubleValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetDoubleAtBigIndex() throws Exception {
        storeableWithValues.getDoubleAt(55);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetDoubleAtWrongIndex() throws Exception {
        storeableWithValues.getDoubleAt(2);
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        assertEquals(storeableWithValues.getBooleanAt(5), (Object) true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBooleanAtBigIndex() throws Exception {
        storeableWithValues.getBooleanAt(55);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetBooleanAtWrongIndex() throws Exception {
        storeableWithValues.getBooleanAt(2);
    }

    @Test
    public void testGetStringAt() throws Exception {
        assertEquals(storeableWithValues.getStringAt(6), (Object) "string");
        assertEquals(storeableWithValues.getStringAt(8), (Object) "another string");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetStringAtBigIndex() throws Exception {
        storeableWithValues.getStringAt(55);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetStringAtWrongIndex() throws Exception {
        storeableWithValues.getStringAt(2);
    }

    @Test
    public void toStringTest() {
        storeableWithValues.setColumnAt(0, 0);
        storeableWithValues.setColumnAt(1, 1L);
        storeableWithValues.setColumnAt(2, null);
        storeableWithValues.setColumnAt(3, null);
        storeableWithValues.setColumnAt(4, null);
        storeableWithValues.setColumnAt(5, true);
        storeableWithValues.setColumnAt(6, "string");
        storeableWithValues.setColumnAt(7, 7);
        storeableWithValues.setColumnAt(8, "string");
        assertEquals("MyStoreable[0,1,,,,true,string,7,string]", storeableWithValues.toString());
    }
}
