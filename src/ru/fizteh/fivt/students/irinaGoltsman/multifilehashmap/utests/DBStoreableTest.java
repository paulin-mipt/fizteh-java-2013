package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBStoreable;

import java.util.ArrayList;
import java.util.List;

public class DBStoreableTest {
    private Storeable row;

    @Before
    public void init() {
        List<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(Integer.class);
        columnTypes.add(Byte.class);
        columnTypes.add(Float.class);
        columnTypes.add(Double.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Long.class);
        row = new DBStoreable(columnTypes);
    }
    // Tests for setColumnAt

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnAtWrongColumnIndex() {
        row.setColumnAt(9, "wrong index");
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnAtMismatchTypesIntegerString() {
        row.setColumnAt(0, "string");
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnAtMismatchTypesByteFloat() {
        row.setColumnAt(1, 1.65f);
    }

    @Test
    public void setColumnAtAndGetColumnAtWork() {
        row.setColumnAt(0, 0);
        row.setColumnAt(1, (byte) 1);
        row.setColumnAt(2, 1.2f);
        row.setColumnAt(3, 1.3d);
        row.setColumnAt(4, true);
        row.setColumnAt(5, "пять");
        row.setColumnAt(6, 6L);
        Assert.assertEquals(0, row.getColumnAt(0));
        Assert.assertEquals((byte) 1, row.getColumnAt(1));
        Assert.assertEquals(1.2f, row.getColumnAt(2));
        Assert.assertEquals(1.3d, row.getColumnAt(3));
        Assert.assertEquals(true, row.getColumnAt(4));
        Assert.assertEquals("пять", row.getColumnAt(5));
        Assert.assertEquals(6L, row.getColumnAt(6));
    }

    // Tests for getColumnAt
    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnAtIndexOutOfBounds() {
        row.getColumnAt(10);
    }

    @Test
    public void setColumnAtAndGetColumnAtNullValue() {
        row.setColumnAt(0, null);
        Object value = row.getColumnAt(0);
        Assert.assertNull(value);
    }

    // IndexOutOfBounds
    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntAtIndexOutOfBounds() {
         row.getIntAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteAtIndexOutOfBounds() {
        row.getByteAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatAtIndexOutOfBounds() {
        row.getFloatAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleAtIndexOutOfBounds() {
        row.getDoubleAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanAtIndexOutOfBounds() {
        row.getBooleanAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringAtIndexOutOfBounds() {
        row.getStringAt(10);
    }

    @Test
    public void getIntAtWork() {
        row.setColumnAt(0, 0);
        Assert.assertEquals(Integer.valueOf(0), row.getIntAt(0));
    }

    @Test
    public void getByteAtWork() {
        byte b = 1;
        row.setColumnAt(1, b);
        Assert.assertEquals(Byte.valueOf(b), row.getByteAt(1));
    }

    @Test
    public void getFloatAtWork() {
        row.setColumnAt(2, 1.2f);
        Assert.assertEquals(Float.valueOf(1.2f), row.getFloatAt(2));
    }

    @Test
    public void getDoubleAtWork() {
        row.setColumnAt(3, 1.2d);
        Assert.assertEquals(Double.valueOf(1.2d), row.getDoubleAt(3));
    }

    @Test
    public void getBooleanAtWork() {
        row.setColumnAt(4, true);
        Assert.assertEquals(true, row.getBooleanAt(4));
    }

    @Test
    public void getStringAtWork() {
        row.setColumnAt(5, "пять");
        Assert.assertEquals("пять", row.getStringAt(5));
    }

    @Test
    public void getLongAtWork() {
        row.setColumnAt(6, 23523535L);
        Assert.assertEquals(Long.valueOf(23523535L), row.getLongAt(6));
    }
}
