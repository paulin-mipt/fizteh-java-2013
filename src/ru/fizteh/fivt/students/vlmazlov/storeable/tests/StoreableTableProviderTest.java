package ru.fizteh.fivt.students.vlmazlov.storeable.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.vlmazlov.storeable.TableRow;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableProviderTest {
    private StoreableTableProvider provider;
    private List<Class<?>> valueTypes1;
    private List<Class<?>> valueTypes2;
    private List<Object> values1;
    private List<Object> values2;
    private List<Object> values3;
    private List<Object> values4;
    private final String root = "StoreableTableProviderTest";

    @Before
    public void setUp() {
        try {
            File tempDir = FileUtils.createTempDir(root, null);
            provider = new StoreableTableProvider(tempDir.getPath(), false);
            valueTypes1 = new ArrayList<Class<?>>() { {
                add(Double.class);
                add(Integer.class);
                add(Boolean.class);
            }};

            valueTypes2 = new ArrayList<Class<?>>() { {
                add(String.class);
                add(Float.class);
                add(Boolean.class);
            }};

            values1 = new ArrayList<Object>() { {
                add(Double.valueOf("1.54"));
                add(Integer.valueOf("123412"));
                add(Boolean.valueOf("false"));
            }};

            values2 = new ArrayList<Object>() { {
                add(Float.valueOf("1.5f"));
                add(new String("123412"));
                add(Boolean.valueOf("false"));
            }};

            values3 = new ArrayList<Object>() { {
                add(Boolean.valueOf("false"));
            }};

            values4 = new ArrayList<Object>() { {
                add(Float.valueOf("1.5f"));
                add(new String("123412"));
                add(Boolean.valueOf("false"));
                add(Double.valueOf("1.54"));
            }};

        } catch (ValidityCheckFailedException ex) {
            Assert.fail("validity check failed: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingNullNameShouldFail() throws IOException {
        provider.createTable(null, valueTypes1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingNullTypesShouldFail() throws IOException {
        provider.createTable("tesTable", (List) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingNullShouldFail() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removingNullShouldFail() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removingNonExistingTableShouldFail() {
        provider.removeTable("testNonExist");
    }

    @Test
    public void gettingNonExistingTableShouldFail() {
        Assert.assertNull("should be null", provider.getTable("testNonExist"));
    }

    @Test
    public void gettingCreatedTable() throws IOException {
        Table created = provider.createTable("testGet", valueTypes1);
        Table firstGet = provider.getTable("testGet");
        Table secondGet = provider.getTable("testGet");
        Assert.assertEquals("should be testGet", "testGet", provider.getTable("testGet").getName());
        Assert.assertSame("getting should returns the same table as create", created, firstGet);
        Assert.assertSame("getting the same table twice should return the same", firstGet, secondGet);
        provider.removeTable("testGet");
    }

    @Test
    public void gettingRemovedTable() throws IOException {
        provider.createTable("testRemove", valueTypes1);
        provider.removeTable("testRemove");
        Assert.assertNull("should be null", provider.getTable("testRemove"));
    }

    @Test
    public void puttingCreatedForTable() throws IOException {
        Table table = provider.createTable("testGet", valueTypes1);
        Storeable row = provider.createFor(table, values1);
        table.put("key1", row);
    }

    @Test(expected = ColumnFormatException.class)
    public void passingWrongTypesShouldFail() throws IOException {
        Table table = provider.createTable("testGet", valueTypes1);
        provider.createFor(table, values2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void tooFewValues() throws IOException {
        Table table = provider.createTable("testGet", valueTypes1);
        provider.createFor(table, values3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void tooManyValues() throws IOException {
        Table table = provider.createTable("testGet", valueTypes1);
        provider.createFor(table, values4);
    }

    @Test
    public void serializeValidity() throws IOException {
        Table table = provider.createTable("testGet", valueTypes1);
        provider.createFor(table, values1);
        Storeable row = provider.createFor(table, values1);
        table.put("key1", row);
        Assert.assertEquals("serialized value isn't correct", 
            "<row><col>1.54</col><col>123412</col><col>false</col></row>",
                provider.serialize(table, row));
    }

    @Test(expected = ColumnFormatException.class)
    public void serializingWrongTypes() throws IOException {
        Table table = provider.createTable("testGet", valueTypes1);
        TableRow wrongRow = new TableRow(valueTypes2);
        wrongRow.setColumnAt(0, "daisy");
        System.out.println(provider.serialize(table, wrongRow));
    }

    @Test
    public void deserializeValidity() throws IOException, ParseException {
        Table table = provider.createTable("testGet", valueTypes1);
        Storeable row = provider.deserialize(table, "<row><col>1.54</col><col>123412</col><col>false</col></row>");

        Assert.assertEquals("deserialized value is incorrect", (Double) row.getDoubleAt(0), (Double) 1.54);
        Assert.assertEquals("deserialized value is incorrect", (Integer) row.getIntAt(1), (Integer) 123412);
        Assert.assertEquals("deserialized value is incorrect", row.getBooleanAt(2), false);
    }

    @Test(expected = ParseException.class)
    public void deserializeInvalidShouldFail() throws IOException, ParseException {
        Table table = provider.createTable("testGet", valueTypes1);
        Storeable row = provider.deserialize(table, "<row><col>1.54</col><col>123412</col><co>false</co></row>");

        Assert.assertEquals("deserialized value is incorrect", (Double) row.getDoubleAt(0), (Double) 1.54);
        Assert.assertEquals("deserialized value is incorrect", (Integer) row.getIntAt(1), (Integer) 123412);
        Assert.assertEquals("deserialized value is incorrect", row.getBooleanAt(2), false);
    }

    @Test(expected = IllegalStateException.class)
    public void closeProviderSize() throws IOException {
        Table table = provider.createTable("testCloseSize", valueTypes1);
        provider.close();
        table.size();
    }

    @Test
    public void closeTableGetTable() throws IOException {
        StoreableTable table = provider.createTable("testGetTable", valueTypes1);
        table.close();
        Assert.assertNotNull("closed table not accessible from provider", provider.getTable("testGetTable"));
        table.close();
    }

    @Test
    public void toStringTest() throws IOException {
        Assert.assertEquals("wrong string representation",
                "StoreableTableProvider[" + provider.getRoot() + "]",
                provider.toString());
    }
} 
