package ru.fizteh.fivt.students.vlmazlov.storeable.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.vlmazlov.storeable.TableRow;

import java.util.ArrayList;
import java.util.List;

public class StoreableTest {
    private TableRow row;

    @Before
    public void setUp() {
        List<Class<?>> valueTypes = new ArrayList<Class<?>>() { {
            add(Double.class);
            add(Integer.class);
            add(Boolean.class);
            add(Float.class);
            add(Long.class);
            add(Byte.class);
            add(String.class);
        }};

        row = new TableRow(valueTypes);

        row.setColumnAt(0, 1.54);
        row.setColumnAt(1, 12312);
        row.setColumnAt(2, false);
        row.setColumnAt(3, 1.2f);
        row.setColumnAt(4, 12432131241244L);
        row.setColumnAt(5, Byte.valueOf((byte) -89));
        row.setColumnAt(6, "adventure");
    }

    @Test
    public void settingToNullShouldNotFail() {
        row.setColumnAt(3, null);
        Assert.assertNull("Null is a valid value", row.getColumnAt(3));
    }

    @Test(expected = ColumnFormatException.class)
    public void settingToWrongType() {
        row.setColumnAt(3, "adversary");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingNegativeColumn() {
        row.getColumnAt(-11);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void gettingTooLargeNumberedColumn() {
        row.getColumnAt(100);
    }

    //wrong gets
    @Test(expected = ColumnFormatException.class)
    public void wrongColumnGetInt() {
        row.getIntAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongColumnGetLong() {
        row.getLongAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongColumnGetByte() {
        row.getByteAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongColumnGetDouble() {
        row.getDoubleAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongColumnGetFloat() {
        row.getFloatAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongColumnGetBoolean() {
        row.getBooleanAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongColumnGetString() {
        row.getStringAt(0);
    }

    //valid gets

    @Test
    public void validColumnGetInt() {
        Assert.assertEquals((Integer) row.getIntAt(1), (Integer) 12312);
    }

    @Test
    public void validColumnGetLong() {
        Assert.assertEquals((Long) row.getLongAt(4), (Long) 12432131241244L);
    }

    @Test
    public void validColumnGetByte() {
        Assert.assertEquals((Byte) row.getByteAt(5), Byte.valueOf((byte) -89));
    }

    @Test
    public void validColumnGetDouble() {
        Assert.assertEquals((Double) row.getDoubleAt(0), (Double) 1.54);
    }

    @Test
    public void validColumnGetFloat() {
        Assert.assertEquals((Float) row.getFloatAt(3), (Float) 1.2f);
    }

    @Test
    public void validColumnGetBoolean() {
        Assert.assertEquals(row.getBooleanAt(2), false);
    }

    @Test
    public void validColumnGetString() {
        Assert.assertEquals(row.getStringAt(6), "adventure");
    }

    @Test
    public void toStringTest() {
        row.setColumnAt(0, 1.54);
        row.setColumnAt(1, 12312);
        row.setColumnAt(2, false);
        row.setColumnAt(3, 1.2f);
        row.setColumnAt(4, 12432131241244L);
        row.setColumnAt(5, null);
        row.setColumnAt(6, "adventure");
        Assert.assertEquals("wrong string representation",
                "TableRow[1.54,12312,false,1.2,12432131241244,,adventure]",
                row.toString());
    }
} 
