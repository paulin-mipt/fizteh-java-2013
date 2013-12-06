package ru.fizteh.fivt.students.msandrikova.storeable;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

public class TableRowTest {
    private TableRow row;
    private static List<Class<?>> columnTypes;
    
    @BeforeClass
    public static void onlyOnce() {
        columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Float.class);
        columnTypes.add(Long.class);
        columnTypes.add(Double.class);
        columnTypes.add(Byte.class);
    }

    @Before
    public void setUp() throws Exception {
        row = new TableRow(columnTypes);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetColumnAt() {
        row.setColumnAt(1, 3);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetColumnAtIndex() {
        row.setColumnAt(17, 3);
    }

    @Test
    public void testGetColumnAt() {
        Object value = "sad";
        row.setColumnAt(2, value);
        assertEquals(row.getColumnAt(2), value);
    }

    @Test
    public void testGetIntAt() {
        Integer value = 7;
        row.setColumnAt(0, value);
        assertEquals(row.getIntAt(0), value);
    }

    @Test
    public void testGetLongAt() {
        Long value = (long) 7;
        row.setColumnAt(4, value);
        assertEquals(row.getLongAt(4), value);
    }

    @Test
    public void testGetByteAt() {
        Byte value = 7;
        row.setColumnAt(6, value);
        assertEquals(row.getByteAt(6), value);
    }

    @Test
    public void testGetFloatAt() {
        Float value = (float) 7.2;
        row.setColumnAt(3, value);
        assertEquals(row.getFloatAt(3), value);
    }

    @Test
    public void testGetDoubleAt() {
        Double value = 7.2;
        row.setColumnAt(5, value);
        assertEquals(row.getDoubleAt(5), value);
    }

    @Test
    public void testGetBooleanAt() {
        Boolean value = true;
        row.setColumnAt(1, value);
        assertEquals(row.getBooleanAt(1), value);
    }

    @Test
    public void testGetStringAt() {
        String value = "test";
        row.setColumnAt(2, value);
        assertEquals(row.getStringAt(2), value);
    }

}
