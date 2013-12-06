package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.util.ArrayList;

public class TableRowTest {

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetIndexUnderOfBounds() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>();
        tempClasses.add(0, Integer.class);
        TableRow test = new TableRow(tempClasses);
        test.setColumnAt(-1, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetIndexOverOfBounds() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>();
        tempClasses.add(0, Integer.class);
        TableRow test = new TableRow(tempClasses);
        test.setColumnAt(100500, 1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetIllegalClass() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>();
        tempClasses.add(0, Integer.class);
        TableRow test = new TableRow(tempClasses);
        test.setColumnAt(0, Integer.toString(14));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnAtIndexUnderOfBounds() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(1);
        tempClasses.add(0, Integer.class);
        TableRow test = new TableRow(tempClasses);
        test.setColumnAt(0, 1);
        test.getColumnAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnAtIndexOverOfBounds() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(1);
        tempClasses.add(0, Integer.class);
        TableRow test = new TableRow(tempClasses);
        test.setColumnAt(0, 1);
        test.getColumnAt(100500);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetIntAt() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(2);
        tempClasses.add(0, Integer.class);
        tempClasses.add(1, String.class);
        TableRow tableRow = new TableRow(tempClasses);
        tableRow.setColumnAt(0, 1);
        tableRow.setColumnAt(1, "aa");
        Assert.assertEquals(tableRow.getIntAt(0).getClass(), Integer.class);
        tableRow.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetLongAt() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(2);
        tempClasses.add(0, Long.class);
        tempClasses.add(1, String.class);
        TableRow tableRow = new TableRow(tempClasses);
        tableRow.setColumnAt(0, new Long(100500));
        tableRow.setColumnAt(1, "aa");
        Assert.assertEquals(tableRow.getLongAt(0).getClass(), Long.class);
        tableRow.getLongAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetByteAt() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(2);
        tempClasses.add(0, Byte.class);
        tempClasses.add(1, String.class);
        TableRow tableRow = new TableRow(tempClasses);
        tableRow.setColumnAt(0, Byte.MIN_VALUE);
        tableRow.setColumnAt(1, "aa");
        Assert.assertEquals(tableRow.getByteAt(0).getClass(), Byte.class);
        tableRow.getByteAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetFloatAt() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(2);
        tempClasses.add(0, Float.class);
        tempClasses.add(1, String.class);
        TableRow tableRow = new TableRow(tempClasses);
        tableRow.setColumnAt(0, new Float(3.14));
        tableRow.setColumnAt(1, "aa");
        Assert.assertEquals(tableRow.getFloatAt(0).getClass(), Float.class);
        tableRow.getFloatAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetDoubleAt() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(2);
        tempClasses.add(0, Double.class);
        tempClasses.add(1, String.class);
        TableRow tableRow = new TableRow(tempClasses);
        tableRow.setColumnAt(0, new Double(3.14));
        tableRow.setColumnAt(1, "aa");
        Assert.assertEquals(tableRow.getDoubleAt(0).getClass(), Double.class);
        tableRow.getDoubleAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetBooleanAt() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(2);
        tempClasses.add(0, Boolean.class);
        tempClasses.add(1, String.class);
        TableRow tableRow = new TableRow(tempClasses);
        tableRow.setColumnAt(0, true);
        tableRow.setColumnAt(1, "aa");
        Assert.assertEquals(tableRow.getBooleanAt(0).getClass(), Boolean.class);
        tableRow.getBooleanAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetStringAt() {
        ArrayList<Class<?>> tempClasses = new ArrayList<>(2);
        tempClasses.add(0, Float.class);
        tempClasses.add(1, String.class);
        TableRow tableRow = new TableRow(tempClasses);
        tableRow.setColumnAt(0, new Float(3.14));
        tableRow.setColumnAt(1, "aa");
        Assert.assertEquals(tableRow.getStringAt(1).getClass(), String.class);
        tableRow.getStringAt(0);
    }

}
