package ru.fizteh.fivt.students.kislenko.proxy.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.kislenko.proxy.MyTable;
import ru.fizteh.fivt.students.kislenko.proxy.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.proxy.Value;

import java.util.ArrayList;
import java.util.List;

public class ValueTest {
    private static MyTableProvider provider;
    private static MyTable table;
    private static Value testingValue;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        provider = new MyTableProvider("./table");
        List<Class<?>> typelist = new ArrayList<Class<?>>();
        typelist.add(Integer.class);
        typelist.add(Long.class);
        typelist.add(Byte.class);
        typelist.add(Float.class);
        typelist.add(Double.class);
        typelist.add(Boolean.class);
        typelist.add(String.class);
        table = provider.createTable("table", typelist);
    }

    @Before
    public void setUp() throws Exception {
        testingValue = (Value) provider.createFor(table);
    }

    @Test
    public void testSetColumnAt() throws Exception {
        testingValue.setColumnAt(0, 100);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetWrongType() throws Exception {
        testingValue.setColumnAt(6, 1488);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetWrongColumnAt() throws Exception {
        // pay attention to the value!
        testingValue.setColumnAt(-1, "Repair the cradle!!!1");
    }

    @Test
    public void testGetBadIntAt() throws Exception {
        Assert.assertNull(testingValue.getColumnAt(0));
    }

    @Test
    public void getAllColumns() throws Exception {
        testingValue.setColumnAt(0, 100);
        Assert.assertEquals(100, testingValue.getColumnAt(0));
        testingValue.setColumnAt(1, (long) 1024 * 1024 * 1024 * 1024);
        Assert.assertEquals((long) 1024 * 1024 * 1024 * 1024, testingValue.getColumnAt(1));
        testingValue.setColumnAt(1, (long) 1024);
        Assert.assertEquals((long) 1024, testingValue.getColumnAt(1));
        testingValue.setColumnAt(2, (byte) 16);
        Assert.assertEquals((byte) 16, testingValue.getColumnAt(2));
        testingValue.setColumnAt(3, (float) 1.05);
        Assert.assertEquals((float) 1.05, testingValue.getColumnAt(3));
        testingValue.setColumnAt(4, 1.0005);
        Assert.assertEquals(1.0005, testingValue.getColumnAt(4));
        testingValue.setColumnAt(5, true);
        Assert.assertEquals(true, testingValue.getColumnAt(5));
        testingValue.setColumnAt(6, "Merge me please...");
        Assert.assertEquals("Merge me please...", testingValue.getColumnAt(6));
    }

    @Test
    public void getAllNullColumns() throws Exception {
        Assert.assertNull(testingValue.getColumnAt(0));
        Assert.assertNull(testingValue.getColumnAt(1));
        Assert.assertNull(testingValue.getColumnAt(2));
        Assert.assertNull(testingValue.getColumnAt(3));
        Assert.assertNull(testingValue.getColumnAt(4));
        Assert.assertNull(testingValue.getColumnAt(5));
        Assert.assertNull(testingValue.getColumnAt(6));
    }

    @Test
    public void testGetAllAt() throws Exception {
        testingValue.setColumnAt(0, 100);
        Assert.assertEquals((Object) 100, testingValue.getIntAt(0));
        testingValue.setColumnAt(1, (long) 1024 * 1024 * 1024 * 1024);
        Assert.assertEquals((Object) ((long) 1024 * 1024 * 1024 * 1024), testingValue.getLongAt(1));
        testingValue.setColumnAt(1, (long) 1024);
        Assert.assertEquals((Object) ((long) 1024), testingValue.getLongAt(1));
        testingValue.setColumnAt(2, (byte) 16);
        Assert.assertEquals((Object) ((byte) 16), testingValue.getByteAt(2));
        testingValue.setColumnAt(3, (float) 1.05);
        Assert.assertEquals((float) 1.05, (Object) testingValue.getFloatAt(3));
        testingValue.setColumnAt(4, 1.0005);
        Assert.assertEquals(1.0005, (Object) testingValue.getDoubleAt(4));
        testingValue.setColumnAt(5, true);
        Assert.assertEquals(true, testingValue.getBooleanAt(5));
        testingValue.setColumnAt(6, "Deadline is coming");
        Assert.assertEquals("Deadline is coming", testingValue.getStringAt(6));
    }

    @Test
    public void getAllNullType() throws Exception {
        Assert.assertNull(testingValue.getIntAt(0));
        Assert.assertNull(testingValue.getLongAt(1));
        Assert.assertNull(testingValue.getByteAt(2));
        Assert.assertNull(testingValue.getFloatAt(3));
        Assert.assertNull(testingValue.getDoubleAt(4));
        Assert.assertNull(testingValue.getBooleanAt(5));
        Assert.assertNull(testingValue.getStringAt(6));
    }

    @Test
    public void toStringTest() throws Exception {
        testingValue.setColumnAt(0, 100);
        testingValue.setColumnAt(1, (long) 1024 * 1024 * 1024 * 1024);
        testingValue.setColumnAt(2, (byte) 16);
        testingValue.setColumnAt(3, (float) 1.05);
        testingValue.setColumnAt(4, 1.0005);
        testingValue.setColumnAt(5, true);
        testingValue.setColumnAt(6, "Deadline is coming");
        Assert.assertEquals("Value[100,1099511627776,16,1.05,1.0005,true,Deadline is coming]", testingValue.toString());
    }

    @Test
    public void toStringNullsTest() throws Exception {
        Assert.assertEquals("Value[,,,,,,]", testingValue.toString());
    }
}
