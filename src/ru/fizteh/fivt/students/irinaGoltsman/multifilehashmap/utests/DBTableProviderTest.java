package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBTableProviderTest {
    private static TableProvider provider;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTableProvider() throws IOException, ParseException {
        provider = new DBTableProvider(rootDBDirectory.newFolder());
    }

    //-------Tests for getTable
    @Test(expected = IllegalArgumentException.class)
    public void getTableNullTableName() {
        provider.getTable(null);
    }

    @Test
    public void getTableNotExistingTable() {
        Assert.assertNull(provider.getTable("notExistingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableErrorTableName() {
        provider.getTable("//\0");
    }

    @Test
    public void getTableRecallShouldReturnTheSameTable() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("tmp", columnTypes);
        Assert.assertEquals(provider.getTable("tmp"), provider.getTable("tmp"));
        provider.removeTable("tmp");
    }

    @Test(expected = IllegalStateException.class)
    public void getTableAfterClosing() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("tmp123", columnTypes);
        ((DBTableProvider) provider).close();
        provider.getTable("tmp123");
    }

    //------Tests for createTable
    @Test(expected = IllegalStateException.class)
    public void createTableAfterClosing() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        ((DBTableProvider) provider).close();
        provider.createTable("tmp123", columnTypes);
    }

    @Test
    public void createTableForExistingTableReturnsNull() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("tmp", columnTypes);
        Assert.assertNull(provider.createTable("tmp", columnTypes));
        provider.removeTable("tmp");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableErrorTableName() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("//\0", columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableNullTableName() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable(null, columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableNullColumnTypes() throws IOException {
        provider.createTable("tmp", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableWithNullColumnTypeShouldFail() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(Integer.class);
        columnTypes.add(null);
        provider.createTable("tmp", columnTypes);
    }

    @Test
    public void createTableRightWritingSignature() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Byte.class);
        types.add(Long.class);
        types.add(Double.class);
        types.add(Float.class);
        types.add(Boolean.class);
        types.add(String.class);
        Table table = provider.createTable("tmp", types);
        if (table == null) {
            provider.removeTable("tmp");
            table = provider.createTable("tmp", types);
        }
        provider.removeTable("tmp");
    }

    //-------Tests for removeTable
    @Test(expected = IllegalStateException.class)
    public void removeTableAfterClosing() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("tmp123", columnTypes);
        ((DBTableProvider) provider).close();
        provider.removeTable("tmp123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableNullNameTable() throws IOException {
        provider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableErrorTableName() throws IOException {
        provider.removeTable("//\0");
    }

    @Test(expected = IllegalStateException.class)
    public void removeTableNotExistingTable() throws IOException {
        provider.removeTable("newNotExistingTable");
    }

    //----Tests for deserialize
    @Test(expected = IllegalStateException.class)
    public void deserializeAfterClosing() throws IOException, ParseException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        Table table = provider.createTable("tmp123", columnTypes);
        ((DBTableProvider) provider).close();
        Storeable row = provider.deserialize(table, "[5, \"пять\"]");
    }

    @Test
    public void deserializeSimpleWork() throws IOException, ParseException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Table table = provider.createTable("tmp", types);
        Storeable row = provider.deserialize(table, "[5, \"пять\"]");
        Assert.assertEquals(5, row.getIntAt(0).intValue());
        Assert.assertEquals("пять", row.getStringAt(1));
        provider.removeTable("tmp");
    }

    @Test(expected = ParseException.class)
    public void deserializeTypesMismatchShouldFail() throws IOException, ParseException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Table table = provider.createTable("tmp", types);
        if (table == null) {
            table = provider.getTable("tmp");
        }
        Storeable row = provider.deserialize(table, "[5.4, \"пять\"]");
        provider.removeTable("tmp");
    }

    @Test
    public void deserializeTypeInferenceByteIntShouldWork() throws IOException, ParseException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Byte.class);
        Table table = provider.createTable("tmp", types);
        if (table == null) {
            table = provider.getTable("tmp");
        }
        Storeable row = provider.deserialize(table, "[5]");
        Byte b = 5;
        Assert.assertEquals(b, row.getByteAt(0));
        provider.removeTable("tmp");
    }

    @Test
    public void deserializeTypeInferenceLongIntShouldWork() throws IOException, ParseException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Long.class);
        Table table = provider.createTable("tmp", types);
        if (table == null) {
            table = provider.getTable("tmp");
        }
        Storeable row = provider.deserialize(table, "[5]");
        Assert.assertEquals(5L, row.getLongAt(0).longValue());
        provider.removeTable("tmp");
    }

    @Test
    public void deserializeTypeInferenceDoubleFloatShouldWork() throws IOException, ParseException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Double.class);
        Table table = provider.createTable("tmp", types);
        if (table == null) {
            table = provider.getTable("tmp");
        }
        Storeable row = provider.deserialize(table, "[5.54]");
        Double d = 5.54;
        Assert.assertEquals(d, row.getDoubleAt(0));
        provider.removeTable("tmp");
    }

    @Test
    public void deserializeTypeRoutineWork() throws IOException, ParseException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Byte.class);
        types.add(Long.class);
        types.add(Double.class);
        types.add(Float.class);
        types.add(Boolean.class);
        types.add(String.class);
        Table table = provider.createTable("tmp", types);
        if (table == null) {
            table = provider.getTable("tmp");
        }
        Storeable row = provider.deserialize(table,
                "[1024,null,1025,1024.1,1024.1,true,\"05bf9c3c5d9031e21babab85fd3bbb3cзначение\"]");
        Integer i = 1024;
        Long l = 1025L;
        Double d = 1024.1;
        Float f = 1024.1f;
        Boolean b = true;
        String s = "05bf9c3c5d9031e21babab85fd3bbb3cзначение";
        Assert.assertEquals(i, row.getIntAt(0));
        Assert.assertEquals(null, row.getByteAt(1));
        Assert.assertEquals(l, row.getLongAt(2));
        Assert.assertEquals(d, row.getDoubleAt(3));
        Assert.assertEquals(f, row.getFloatAt(4));
        Assert.assertEquals(b, row.getBooleanAt(5));
        Assert.assertEquals(s, row.getStringAt(6));
        provider.removeTable("tmp");
    }
}
