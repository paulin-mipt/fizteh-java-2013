package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapStoreable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestFileMapStoreable {

    private FileMapStoreable stIntString;
    private FileMapStoreable stAll;
    private FileMapStoreable stInt;

    @Before
    public void setUp() {
        List<Class<?>> list1 = new ArrayList<>();
        list1.add(Integer.class);
        list1.add(String.class);
        stIntString = new FileMapStoreable(list1);

        List<Class<?>> list2 = new ArrayList<>();
        list2.add(Integer.class);
        list2.add(Long.class);
        list2.add(Byte.class);
        list2.add(Float.class);
        list2.add(Double.class);
        list2.add(Boolean.class);
        list2.add(String.class);
        stAll = new FileMapStoreable(list2);

        List<Class<?>> list3 = new ArrayList<>();
        list3.add(Integer.class);
        stInt = new FileMapStoreable(list3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongColumn() {
        stIntString.getColumnAt(3);
    }

    @Test(expected = ColumnFormatException.class)
    public void setWrongType() {
        stIntString.setColumnAt(0, "str");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setWrongNumber() {
        stIntString.setColumnAt(3, "str");
    }

    @Test()
    public void getDifferentType() {
        stIntString.setColumnAt(0, 1);
        stIntString.setColumnAt(1, "qwe");
        assertEquals(stIntString.getIntAt(0), stIntString.getColumnAt(0));
        assertEquals(stIntString.getStringAt(1), stIntString.getColumnAt(1));
        assertEquals((int) stIntString.getIntAt(0), 1);
        assertEquals(stIntString.getStringAt(1), "qwe");
    }

    @Test()
    public void setGetAll() throws IOException {
        stAll.setColumnAt(0, 1);
        stAll.setColumnAt(1, (long) 3);
        stAll.setColumnAt(2, (byte) 2);
        stAll.setColumnAt(3, (float) 2.1);
        stAll.setColumnAt(4, 2.2);
        stAll.setColumnAt(5, true);
        stAll.setColumnAt(6, "qwe");
        assertEquals(Integer.valueOf(1), stAll.getIntAt(0));
        assertEquals(Long.valueOf(3), stAll.getLongAt(1));
        assertEquals(Byte.valueOf((byte) 2), stAll.getByteAt(2));
        assertEquals(Float.valueOf((float) 2.1), stAll.getFloatAt(3));
        assertEquals(Double.valueOf(2.2), stAll.getDoubleAt(4));
        assertEquals(true, stAll.getBooleanAt(5));
        assertEquals("qwe", stAll.getStringAt(6));
    }

    @Test()
    public void setGetNull() {
        assertEquals(null, stInt.getIntAt(0));
        assertEquals(null, stInt.getColumnAt(0));
        stInt.setColumnAt(0, null);
        assertEquals(null, stInt.getIntAt(0));
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongType() {
        stInt.getBooleanAt(0);
    }

    @Test()
    public void checkEqual() {
        List<Class<?>> list3 = new ArrayList<>();
        list3.add(Integer.class);
        FileMapStoreable stInt2 = new FileMapStoreable(list3);
        stInt2.setColumnAt(0, 1);
        stInt.setColumnAt(0, 1);
        assertTrue(stInt2.equals(stInt));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongIntColumn() {
        stIntString.getIntAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongByteColumn() {
        stIntString.getByteAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongLongColumn() {
        stIntString.getLongAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongDoubleColumn() {
        stIntString.getDoubleAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongFloatColumn() {
        stIntString.getFloatAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongBooleanColumn() {
        stIntString.getBooleanAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWrongStringColumn() {
        stIntString.getStringAt(3);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongIntType() {
        stAll.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongByteType() {
        stInt.getByteAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongDoubleType() {
        stInt.getDoubleAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongFloatType() {
        stInt.getFloatAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongLongType() {
        stInt.getLongAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongBooleanType() {
        stInt.getBooleanAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getWrongStringType() {
        stInt.getStringAt(0);
    }

    @Test()
    public void correctToString() throws IOException {
        stIntString.setColumnAt(0, 1);
        stIntString.setColumnAt(1, "qwe");
        assertEquals(stIntString.toString(),
                String.format("%s[%s]", "FileMapStoreable", "1,qwe"));
    }
}
