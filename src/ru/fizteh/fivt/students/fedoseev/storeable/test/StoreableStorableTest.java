package ru.fizteh.fivt.students.fedoseev.storeable.test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.fedoseev.storeable.ColumnTypes;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTable;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTableProvider;

import java.io.IOException;

public class StoreableStorableTest {
    private Storeable storable;
    private StoreableTable table;
    private StoreableTableProvider tp;

    public StoreableStorableTest() throws IOException {
        tp = new StoreableTableProvider();

        table = tp.createTable("hell", ColumnTypes.getTypesList());
    }

    @Before
    public void setUp() throws Exception {
        storable = tp.createFor(table);
    }

    @Test
    public void testSetColumnAt() throws Exception {
        storable.setColumnAt(1, (byte) 111);
    }

    @Test
    public void testGetColumnsAt() throws Exception {
        storable.setColumnAt(0, false);
        storable.setColumnAt(1, (byte) 13);
        storable.setColumnAt(2, 101.101);
        storable.setColumnAt(3, (float) 010.010);
        storable.setColumnAt(4, 100500);
        storable.setColumnAt(5, (long) 2 << 55);
        storable.setColumnAt(6, "bitter end");

        Assert.assertEquals(false, storable.getColumnAt(0));
        Assert.assertEquals((byte) 13, storable.getColumnAt(1));
        Assert.assertEquals(101.101, storable.getColumnAt(2));
        Assert.assertEquals((float) 010.010, storable.getColumnAt(3));
        Assert.assertEquals(100500, storable.getColumnAt(4));
        Assert.assertEquals((long) 2 << 55, storable.getColumnAt(5));
        Assert.assertEquals("bitter end", storable.getColumnAt(6));
    }

    @Test
    public void testGetNullColumnsAt() throws Exception {
        Assert.assertNull(storable.getColumnAt(0));
        Assert.assertNull(storable.getColumnAt(1));
        Assert.assertNull(storable.getColumnAt(2));
        Assert.assertNull(storable.getColumnAt(3));
        Assert.assertNull(storable.getColumnAt(4));
        Assert.assertNull(storable.getColumnAt(5));
        Assert.assertNull(storable.getColumnAt(6));
    }

    @Test
    public void testGetTypesAt() throws Exception {
        storable.setColumnAt(0, false);
        storable.setColumnAt(1, (byte) 13);
        storable.setColumnAt(2, 101.101);
        storable.setColumnAt(3, (float) 010.010);
        storable.setColumnAt(4, 100500);
        storable.setColumnAt(5, (long) 2 << 55);
        storable.setColumnAt(6, "bitter end");

        Assert.assertEquals((Object) false, storable.getBooleanAt(0));
        Assert.assertEquals((Object) (byte) 13, storable.getByteAt(1));
        Assert.assertEquals(101.101, storable.getDoubleAt(2));
        Assert.assertEquals((float) 010.010, storable.getFloatAt(3));
        Assert.assertEquals((Object) 100500, storable.getIntAt(4));
        Assert.assertEquals((Object) ((long) 2 << 55), storable.getLongAt(5));
        Assert.assertEquals("bitter end", storable.getStringAt(6));
    }

    @Test
    public void testGetNullTypesAt() throws Exception {
        Assert.assertNull(storable.getBooleanAt(0));
        Assert.assertNull(storable.getByteAt(1));
        Assert.assertNull(storable.getDoubleAt(2));
        Assert.assertNull(storable.getFloatAt(3));
        Assert.assertNull(storable.getIntAt(4));
        Assert.assertNull(storable.getLongAt(5));
        Assert.assertNull(storable.getStringAt(6));
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetInvalidTypeAt() throws Exception {
        storable.setColumnAt(0, "suicide");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetInvalidColumnAt() throws Exception {
        storable.setColumnAt(-666, "KABOOM");
    }
}
