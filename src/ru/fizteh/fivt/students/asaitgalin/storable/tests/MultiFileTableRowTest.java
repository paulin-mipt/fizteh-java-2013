package ru.fizteh.fivt.students.asaitgalin.storable.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.asaitgalin.storable.MultiFileTableRow;

import java.util.ArrayList;
import java.util.List;

public class MultiFileTableRowTest {
    private MultiFileTableRow row;

    @Before
    public void setUp() throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Integer.class);
        classes.add(Long.class);
        classes.add(Byte.class);
        classes.add(Float.class);
        classes.add(Double.class);
        classes.add(Boolean.class);
        classes.add(String.class);
        row = new MultiFileTableRow(classes);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetColumnAtWitWrongValue() throws Exception {
        row.setColumnAt(0, 3.14f);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetColumnAtIndexOutOfBounds() throws Exception {
        row.setColumnAt(52, 3);
    }

    @Test
    public void testSetColumnAt() throws Exception {
        row.setColumnAt(0, 54);;
        Assert.assertEquals(row.getColumnAt(0), 54);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnAtIndexOutOfBounds() throws Exception {
        row.getColumnAt(-1);
    }

    @Test
    public void testGetColumnAt() throws Exception {
        row.setColumnAt(6, "value");
        Assert.assertNotNull(row.getColumnAt(6));
        Assert.assertEquals(row.getColumnAt(6), "value");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntAtIndexOutOfBounds() throws Exception {
        row.getIntAt(-5);
    }

    @Test
    public void testGetIntAt() throws Exception {
        row.setColumnAt(0, 12);
        Assert.assertNotNull(row.getIntAt(0));
        Assert.assertEquals(row.getIntAt(0), Integer.valueOf(12));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetLongAtIndexOutOfBounds() throws Exception {
        row.getLongAt(100);
    }

    @Test
    public void testGetLongAt() throws Exception {
        row.setColumnAt(1, 1234567890L);
        Assert.assertNotNull(row.getLongAt(1));
        Assert.assertEquals(row.getLongAt(1), Long.valueOf(1234567890L));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetByteAtIndexOutOfBounds() throws Exception {
        row.getByteAt(-5);
    }

    @Test
    public void testGetByteAt() throws Exception {
        byte b = 100;
        row.setColumnAt(2, Byte.valueOf(b));
        Assert.assertNotNull(row.getByteAt(2));
        Assert.assertEquals(row.getByteAt(2), Byte.valueOf(b));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetFloatAtIndexOutOfBounds() throws Exception {
        row.getFloatAt(100);
    }

    @Test
    public void testGetFloatAt() throws Exception {
        row.setColumnAt(3, 3.14f);
        Assert.assertNotNull(row.getFloatAt(3));
        Assert.assertEquals(row.getFloatAt(3), Float.valueOf(3.14f));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetDoubleAtIndexOutOfBounds() throws Exception {
        row.getDoubleAt(-1);
    }

    @Test
    public void testGetDoubleAt() throws Exception {
        row.setColumnAt(4, 3.14);
        Assert.assertNotNull(row.getDoubleAt(4));
        Assert.assertEquals(row.getDoubleAt(4), Double.valueOf(3.14));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBooleanAtIndexOutOfBounds() throws Exception {
        row.getBooleanAt(-5);
    }

    @Test
    public void testGetBooleanAt() throws Exception {
        row.setColumnAt(5, true);
        Assert.assertNotNull(row.getBooleanAt(5));
        Assert.assertEquals(row.getBooleanAt(5), true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetStringAtIndexOutOfBounds() throws Exception {
        row.getStringAt(100);
    }

    @Test
    public void testGetStringAt() throws Exception {
        row.setColumnAt(6, "value");
        Assert.assertNotNull(row.getStringAt(6));
        Assert.assertEquals(row.getStringAt(6), "value");
    }

    @Test
    public void testSetColumnAtNull() throws Exception {
        row.setColumnAt(4, null);
    }

    @Test
    public void testGetColumnAtNull() throws Exception {
        row.setColumnAt(5, null);
        Assert.assertNull(row.getBooleanAt(5));
        Assert.assertNull(row.getColumnAt(5));
    }
}
