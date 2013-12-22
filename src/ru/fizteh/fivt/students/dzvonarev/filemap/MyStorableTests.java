package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyStorableTests {

    private MyStoreable value;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        TableProvider provider = factory.create(folder.newFolder().getCanonicalPath());
        List<Class<?>> type = new ArrayList<>();
        type.add(Integer.class);
        type.add(String.class);
        type.add(Long.class);
        type.add(Byte.class);
        type.add(Boolean.class);
        type.add(Float.class);
        type.add(Double.class);
        value = new MyStoreable(provider.createTable("table", type));
        value.setColumnAt(0, -666);
        value.setColumnAt(1, "string");
        value.setColumnAt(2, 8L);
        value.setColumnAt(3, Byte.valueOf((byte) 5));
        value.setColumnAt(4, false);
        value.setColumnAt(5, 2.71f);
        value.setColumnAt(6, 3.14);
    }

    @Test
    public void getIntFromColumn() {
        Assert.assertEquals(value.getIntAt(0), (Integer) (-666));
    }

    @Test
    public void getStringFromColumn() {
        Assert.assertEquals(value.getStringAt(1), "string");
    }

    @Test
    public void getLongFromColumn() {
        Assert.assertEquals(value.getLongAt(2), (Long) 8L);
    }

    @Test
    public void getByteFromColumn() {
        Assert.assertEquals(value.getByteAt(3), Byte.valueOf((byte) 5));
    }

    @Test
    public void getBooleanFromColumn() {
        Assert.assertEquals(value.getBooleanAt(4), false);
    }

    @Test
    public void getFloatFromColumn() {
        Assert.assertEquals(value.getFloatAt(5), (Float) 2.71f);
    }

    @Test
    public void getDoubleFromColumn() {
        Assert.assertEquals(value.getDoubleAt(6), (Double) 3.14);
    }

    @Test(expected = ColumnFormatException.class)
    public void getIntFromWrongColumn() {
        value.getIntAt(6);
    }

    @Test(expected = ColumnFormatException.class)
    public void getDoubleFromWrongColumn() {
        value.getDoubleAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getFloatFromWrongColumn() {
        value.getFloatAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getStringFromWrongColumn() {
        value.getStringAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getLongFromWrongColumn() {
        value.getLongAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getByteFromWrongColumn() {
        value.getByteAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getBooleanFromWrongColumn() {
        value.getBooleanAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void setValueToWrongColumn() {
        value.setColumnAt(0, true);
    }

    @Test
    public void setNull() {
        value.setColumnAt(0, null);
        Assert.assertNull("set null", value.getColumnAt(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void wrongColumnIndex() {
        value.getColumnAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void wrongColumnIndex2() {
        value.getColumnAt(100500);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleFromBadColumn() {
        value.getDoubleAt(100499);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatFromBadColumn() {
        value.getFloatAt(100498);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteFromBadColumn() {
        value.getByteAt(100497);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringFromBadColumn() {
        value.getStringAt(100496);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanFromBadColumn() {
        value.getBooleanAt(100495);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongFromBadColumn() {
        value.getLongAt(100494);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntFromBadColumn() {
        value.getIntAt(100493);
    }

}
