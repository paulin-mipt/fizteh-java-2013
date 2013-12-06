package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class StoreableCommandsTest {    
    Storeable tempStoreable = null;
    
    @Before
    public void initStoreable() throws IOException {
        List<Class<?>> types = new ArrayList();
        types.add(int.class);       //#0
        types.add(boolean.class);   //#1
        types.add(float.class);     //#2
        types.add(Double.class);    //#3
        types.add(byte.class);      //#4
        types.add(Long.class);      //#5
        types.add(String.class);    //#6
        tempStoreable = new StoreableCommands(types);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void setNegativeIndex() {
        tempStoreable.setColumnAt(-1, "hello");
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void setBigIndex() {
        tempStoreable.setColumnAt(7, "hello");
    }
    
    @Test (expected = ColumnFormatException.class)
    public void setIncorrectType() {
        tempStoreable.setColumnAt(1, 1);
    }
    
    @Test
    public void setNullValue() {
        tempStoreable.setColumnAt(2, null);
    }
    
    @Test
    public void setCorrectValue() {
        tempStoreable.setColumnAt(6, "null");
        tempStoreable.setColumnAt(3, (double) 1);
        tempStoreable.setColumnAt(2, (float) 1);
        tempStoreable.setColumnAt(0, (int) 12);
        tempStoreable.setColumnAt(1, (boolean) true);
        tempStoreable.setColumnAt(4, (byte) 1);
        tempStoreable.setColumnAt(5, (long) 100000000);
    }
    
    @Test
    public void getCorrectValue() {
        tempStoreable.setColumnAt(1, null);
        tempStoreable.setColumnAt(3, (double) 1);
        Object value = tempStoreable.getColumnAt(1);
        assertNull("Should be null", value);
        tempStoreable.getDoubleAt(3);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getIncorrectType() {
        tempStoreable.setColumnAt(2, (float) 1);
        tempStoreable.getDoubleAt(2);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void getNegativeIndex() {
        tempStoreable.getColumnAt(-1);
        tempStoreable.getIntAt(-1);
        tempStoreable.getFloatAt(-1);
        tempStoreable.getByteAt(-1);
        tempStoreable.getDoubleAt(-1);
        tempStoreable.getBooleanAt(-1);
        tempStoreable.getLongAt(-1);
        tempStoreable.getStringAt(-1);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void getBigIndex() {
        tempStoreable.getColumnAt(7);
        tempStoreable.getIntAt(7);
        tempStoreable.getFloatAt(7);
        tempStoreable.getByteAt(7);
        tempStoreable.getDoubleAt(7);
        tempStoreable.getBooleanAt(7);
        tempStoreable.getLongAt(7);
        tempStoreable.getStringAt(7);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getIntAtIncorrect() {
        tempStoreable.setColumnAt(1, (boolean) true);
        tempStoreable.getIntAt(1);
    }
    
    @Test
    public void getIntCorrect() {
        tempStoreable.setColumnAt(0, (int) 50);
        Integer currentValue = tempStoreable.getIntAt(0);
        assertTrue("value should be 50", currentValue.equals(50));
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getLongAtIncorrect() {
        tempStoreable.setColumnAt(1, (boolean) true);
        tempStoreable.getLongAt(1);
    }
    
    @Test
    public void getLongCorrect() {
        tempStoreable.setColumnAt(5, (long) 1000000000);
        Long currentValue = tempStoreable.getLongAt(5);
        assertTrue("value should be 1000000000", currentValue.equals((long) 1000000000));
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getByteAtIncorrect() {
        tempStoreable.setColumnAt(1, (boolean) true);
        tempStoreable.getByteAt(1);
    }
    
    @Test
    public void getByteCorrect() {
        tempStoreable.setColumnAt(4, (byte) 2);
        byte currentValue = tempStoreable.getByteAt(4);
        assertTrue("value should be 2", currentValue == (byte) 2);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getFloatAtIncorrect() {
        tempStoreable.setColumnAt(1, (boolean) true);
        tempStoreable.getFloatAt(1);
    }
    
    @Test
    public void getFloatCorrect() {
        tempStoreable.setColumnAt(2, (float) 2.45);
        float currentValue = tempStoreable.getFloatAt(2);
        assertTrue("value should be 2.45", currentValue == (float) 2.45);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getDoubleAtIncorrect() {
        tempStoreable.setColumnAt(1, (boolean) true);
        tempStoreable.getDoubleAt(1);
    }
    
    @Test
    public void getDoubleCorrect() {
        tempStoreable.setColumnAt(3, (double) 100500.47483);
        Double currentValue = tempStoreable.getDoubleAt(3);
        assertTrue("value should be 100500.47483", currentValue == (double) 100500.47483);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getBooleanAtIncorrect() {
        tempStoreable.setColumnAt(0, (int) 50);
        tempStoreable.getBooleanAt(0);
    }
    
    @Test
    public void getBooleanCorrect() {
        tempStoreable.setColumnAt(1, (boolean) false);
        boolean currentValue = tempStoreable.getBooleanAt(1);
        assertTrue("value should be <false>", currentValue == (boolean) false);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getStringAtIncorrect() {
        tempStoreable.setColumnAt(0, (int) 50);
        tempStoreable.getStringAt(0);
    }
    
    @Test
    public void getStrnigCorrect() {
        tempStoreable.setColumnAt(6, "value");
        String currentValue = tempStoreable.getStringAt(6);
        assertTrue("value should be \"value\"", currentValue.equals("value"));
    }
}
