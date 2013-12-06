package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.junittests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableDataValue;

import java.util.ArrayList;
import java.util.List;

public class StoreableDataValueTest {
    Storeable storeableValue;
    List<Class<?>> columnTypes;

    @Before
    public void setUp() throws Exception {
        columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Long.class);
        columnTypes.add(Float.class);
        columnTypes.add(Double.class);
        columnTypes.add(Byte.class);
        storeableValue = new StoreableDataValue(columnTypes);
    }

    @Test
    public void setValidColumnAt() throws Exception {
        storeableValue.setColumnAt(0, 5);
        Assert.assertEquals(storeableValue.getIntAt(0).intValue(), 5);
        Assert.assertNull(storeableValue.getColumnAt(2));
    }

    @Test(expected = ColumnFormatException.class)
    public void setNotValidColummnFormatShouldFail() throws Exception {
        storeableValue.setColumnAt(0, "qwerty");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setNotValidColumnIndexShouldFail() throws Exception {
        storeableValue.setColumnAt(56, "index");
    }

    @Test
    public void getValidColumnIndex() throws Exception {
        storeableValue.setColumnAt(0, 5);
        storeableValue.setColumnAt(1, true);
        Assert.assertNull(storeableValue.getColumnAt(2));
        Assert.assertEquals(storeableValue.getColumnAt(0), 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getNotValidColumnIndexShouldFail() throws Exception {
        storeableValue.getColumnAt(560);
    }

    @Test
    public void getValidLongAt() throws Exception {
        Assert.assertNull(storeableValue.getLongAt(3));
        storeableValue.setColumnAt(3, new Long(7865));
        Assert.assertEquals(storeableValue.getLongAt(3).longValue(), 7865);
    }

    @Test(expected = ColumnFormatException.class)
    public void getNotValidColumnLongShouldFail() throws Exception {
        storeableValue.getLongAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongNotValidIndexShouldFail() throws Exception {
        storeableValue.getLongAt(25);
    }

    @Test
    public void getValidByteAt() throws Exception {
        Assert.assertNull(storeableValue.getByteAt(6));
        storeableValue.setColumnAt(6, new Byte("56"));
        Assert.assertEquals(storeableValue.getByteAt(6), new Byte("56"));
    }

    @Test(expected = ColumnFormatException.class)
    public void getNotValidColumnByteShouldFail() throws Exception {
        storeableValue.getByteAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteNotValidIndexShouldFail() throws Exception {
        storeableValue.getByteAt(56);
    }

    @Test
    public void getValidFloatAt() throws Exception {
        Assert.assertNull(storeableValue.getFloatAt(4));
        storeableValue.setColumnAt(4, new Float(56.25));
        Assert.assertEquals(storeableValue.getFloatAt(4).floatValue(), new Float(56.25));
    }

    @Test(expected = ColumnFormatException.class)
    public void getNotValidColumnFloatShouldFail() throws Exception {
        storeableValue.getFloatAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatNotValidIndexShouldFail() throws Exception {
        storeableValue.getFloatAt(728);
    }

    @Test
    public void getValidDoubleAt() throws Exception {
        Assert.assertNull(storeableValue.getDoubleAt(5));
        storeableValue.setColumnAt(5, 789.45);
        Assert.assertEquals(storeableValue.getDoubleAt(5).doubleValue(), 789.45);
    }

    @Test(expected = ColumnFormatException.class)
    public void getNotValidColumnDoubleShouldFail() throws Exception {
        storeableValue.getDoubleAt(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleNotValidIndexShouldFail() throws Exception {
        storeableValue.getDoubleAt(24);
    }

    @Test
    public void getValidBooleanAt() throws Exception {
        Assert.assertNull(storeableValue.getBooleanAt(1));
        storeableValue.setColumnAt(1, false);
        Assert.assertEquals(storeableValue.getBooleanAt(1).booleanValue(), false);
    }

    @Test(expected = ColumnFormatException.class)
    public void getNotValidColumnBooleanShouldFail() throws Exception {
        storeableValue.getBooleanAt(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanNotValidIndexShouldFail() throws Exception {
        storeableValue.getBooleanAt(55);
    }

    @Test
    public void getValidStringAt() throws Exception {
        Assert.assertNull(storeableValue.getStringAt(2));
        storeableValue.setColumnAt(2, "qwerty");
        Assert.assertEquals(storeableValue.getStringAt(2), "qwerty");
    }

    @Test(expected = ColumnFormatException.class)
    public void getNotValidColumnStringShouldFail() throws Exception {
        storeableValue.getStringAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringNotValidIndexShouldFail() throws Exception {
        storeableValue.getStringAt(31);
    }

}
