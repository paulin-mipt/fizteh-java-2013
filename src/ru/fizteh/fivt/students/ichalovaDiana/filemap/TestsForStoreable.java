package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class TestsForStoreable {
    
    Storeable value1;
    
    @Test
    public void storeableBoolean() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, true);
        Assert.assertEquals(true, value1.getColumnAt(0));
        Assert.assertEquals(true, value1.getBooleanAt(0));
    }
    
    @Test(expected = ColumnFormatException.class)
    public void storeableBooleanWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "true");
    }
    
    @Test
    public void storeableInt() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 666);
        Assert.assertEquals(666, value1.getColumnAt(0));
        Assert.assertEquals((Integer) 666, value1.getIntAt(0));
    }
    
    @Test(expected = ColumnFormatException.class)
    public void storeableIntWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5.3);
    }
    
    @Test
    public void storeableFloat() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Float.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5.3f);
        Assert.assertEquals(5.3f, value1.getColumnAt(0));
        Assert.assertEquals((Float) 5.3f, value1.getFloatAt(0));
    }
    
    @Test(expected = ColumnFormatException.class)
    public void storeableFloatWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Float.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "5.3");
    }
    
    @Test
    public void storeableDouble() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Double.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5.3);
        Assert.assertEquals(5.3, value1.getColumnAt(0));
        Assert.assertEquals((Double) 5.3, value1.getDoubleAt(0));
    }
    
    @Test(expected = ColumnFormatException.class)
    public void storeableDoubleWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Double.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "5.3");
    }
    
    @Test
    public void storeableByte() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Byte.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, (byte) 5);
        Assert.assertEquals((byte) 5, value1.getColumnAt(0));
        Assert.assertEquals((Byte) (byte) 5, value1.getByteAt(0));
    }
    
    @Test(expected = ColumnFormatException.class)
    public void storeableByteWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Byte.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "a");
    }
    
    @Test
    public void storeableLong() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Long.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5L);
        Assert.assertEquals(5L, value1.getColumnAt(0));
        Assert.assertEquals((Long) 5L, value1.getLongAt(0));
    }
    
    @Test(expected = ColumnFormatException.class)
    public void storeableLongWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Long.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "a");
    }
    
    @Test
    public void storeableString() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "String");
        Assert.assertEquals("String", value1.getColumnAt(0));
        Assert.assertEquals("String", value1.getStringAt(0));
    }
    
    @Test(expected = ColumnFormatException.class)
    public void storeableStringWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5L);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableWrongSet() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(1, "AA");
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableGetStringWrongIndex() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "AA");
        value1.getStringAt(1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableGetBooleanWrongIndex() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, true);
        value1.getBooleanAt(1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableGetIntegerWrongIndex() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5);
        value1.getIntAt(1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableGetDoubleWrongIndex() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Double.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5.3);
        value1.getDoubleAt(1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableGetFloatWrongIndex() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Float.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5.3f);
        value1.getFloatAt(1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableGetLongWrongIndex() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Long.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, 5L);
        value1.getFloatAt(1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void storeableGetByteWrongIndex() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Byte.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, (byte) 5);
        value1.getFloatAt(1);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void setStringInsteadOfDouble() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Double.class);
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, "5L");
    }
}
