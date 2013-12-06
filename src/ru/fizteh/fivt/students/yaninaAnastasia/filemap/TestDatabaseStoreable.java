package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class TestDatabaseStoreable {
    Storeable storeable;

    @Before
    public void setUp() {
        List<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(Integer.class);
        columnTypes.add(String.class);
        columnTypes.add(Long.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(Double.class);
        columnTypes.add(Float.class);
        storeable = new DatabaseStoreable(columnTypes);
    }

    @After
    public void tearDown() throws Exception {
        storeable = null;
    }

    @Test
    public void putEmpty() {
        storeable.setColumnAt(1, "");
    }

    @Test
    public void putOnlyWhiteSpaces() {
        storeable.setColumnAt(1, "     ");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putKeyBelowZero() {
        storeable.setColumnAt(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putBigKey() {
        storeable.setColumnAt(10, null);
    }

    @Test
    public void correctTestOne() {
        storeable.setColumnAt(1, "One");
    }

    @Test
    public void correctTestTwo() {
        storeable.setColumnAt(0, 1994);
    }

    @Test(expected = ColumnFormatException.class)
    public void testIntIncorrectType() {
        storeable.setColumnAt(1, 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIntBoundsException() {
        storeable.setColumnAt(10, 12);
    }

    @Test(expected = ColumnFormatException.class)
    public void testStringIncorrectType() {
        storeable.setColumnAt(2, "testing in progress");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testStringBoundsException() {
        storeable.setColumnAt(10, "testing");
    }

    @Test(expected = ColumnFormatException.class)
    public void testIntColumnFormatExc() {
        storeable.setColumnAt(1, "asdasd");
        storeable.getIntAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIntOutOfBoundsExc() {
        storeable.getIntAt(10);
    }

    @Test
    public void testColumnFormatIntRight() {
        storeable.setColumnAt(0, 10);
        Assert.assertTrue(storeable.getIntAt(0) == 10);
    }

    @Test(expected = ColumnFormatException.class)
    public void testStringColumnFormatExc() {
        storeable.setColumnAt(2, 1000);
        storeable.getStringAt(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testStringOutOfBoundsExc() {
        storeable.getStringAt(10);
    }

    @Test
    public void testColumnFormatStringRight() {
        storeable.setColumnAt(1, "a");
        Assert.assertTrue(storeable.getStringAt(1).equals("a"));
    }

    @Test(expected = ColumnFormatException.class)
    public void testLongColumnFormatExc() {
        storeable.setColumnAt(3, true);
        storeable.getLongAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testLongOutOfBoundsExc() {
        storeable.getLongAt(10);
    }

    @Test
    public void testColumnFormatLongRight() {
        storeable.setColumnAt(2, 10L);
        Assert.assertTrue(storeable.getLongAt(2) == 10);
    }

    @Test(expected = ColumnFormatException.class)
    public void testBooleanColumnFormatExc() {
        storeable.setColumnAt(4, 10.5);
        storeable.getBooleanAt(4);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBooleanOutOfBoundsExc() {
        storeable.getBooleanAt(10);
    }

    @Test
    public void testColumnFormatBooleanRight() {
        storeable.setColumnAt(3, true);
        Assert.assertTrue(storeable.getBooleanAt(3));
    }

    @Test(expected = ColumnFormatException.class)
    public void testDoubleColumnFormatExc() {
        storeable.setColumnAt(5, 10.5d);
        storeable.getDoubleAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testDoubleOutOfBoundsExc() {
        storeable.getDoubleAt(10);
    }

    @Test
    public void testColumnFormatDoubleRight() {
        storeable.setColumnAt(4, 10.4d);
        Assert.assertTrue(storeable.getDoubleAt(4) == 10.4);
    }

    @Test(expected = ColumnFormatException.class)
    public void testFloatColumnFormatExc() {
        storeable.setColumnAt(1, "1000");
        storeable.getFloatAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testFloatOutOfBoundsExc() {
        storeable.getFloatAt(10);
    }

    @Test
    public void testColumnFormatFloatRight() {
        storeable.setColumnAt(5, 10f);
        Assert.assertTrue(storeable.getFloatAt(5) == 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsExceptionMore() {
        storeable.setColumnAt(-10, 5);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatException() {
        storeable.setColumnAt(1, true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIndexOutOfBoundsException() {
        storeable.getColumnAt(20);
    }

    @Test
    public void testGetColumnNoException() {
        storeable.setColumnAt(0, 10);
        Assert.assertTrue(storeable.getColumnAt(0) == 10);
    }

    @Test
    public void testSetColumnNoException() {
        storeable.setColumnAt(1, "a");
        Assert.assertTrue(storeable.getColumnAt(1).equals("a"));
    }

    @Test
    public void testToString() throws Exception {
        storeable.setColumnAt(0, 5);
        storeable.setColumnAt(1, "Five");
        storeable.setColumnAt(2, 5L);
        storeable.setColumnAt(3, true);
        storeable.setColumnAt(4, 5.5);
        storeable.setColumnAt(5, 5.5f);
        Assert.assertEquals(storeable.toString(), "DatabaseStoreable[5,Five,5,true,5.5,5.5]");
    }
}
