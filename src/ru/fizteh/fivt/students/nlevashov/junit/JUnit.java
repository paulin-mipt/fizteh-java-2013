package ru.fizteh.fivt.students.nlevashov.junit;

import org.junit.*;
import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.nlevashov.factory.*;
import ru.fizteh.fivt.students.nlevashov.storable.Storable;
import ru.fizteh.fivt.storage.structured.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class JUnit {
    TableProviderFactory factory;
    TableProvider provider;
    Table table;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void providerCreatingPlatformMaking() throws IOException {
        factory = new MyTableProviderFactory();
        assertNotNull(factory);
        Path tfPath = tempFolder.getRoot().toPath();
        Files.createDirectory(tfPath.resolve("factory1"));
        Files.createDirectory(tfPath.resolve("factory1").resolve("emptyDirectory"));

        Files.createDirectory(tfPath.resolve("factory2"));
        Files.createDirectory(tfPath.resolve("factory2").resolve("directoryWithFile"));
        Files.createFile(tfPath.resolve("factory2").resolve("directoryWithFile").resolve("someFile"));
        Files.createFile(tfPath.resolve("factory2").resolve("directoryWithFile").resolve("signature.tsv"));

        Files.createDirectory(tfPath.resolve("factory3"));
        Files.createDirectory(tfPath.resolve("factory3").resolve("directoryWithWrongNameDirectory"));
        Files.createDirectory(tfPath.resolve("factory3").resolve("directoryWithWrongNameDirectory")
                .resolve("12.dat"));
        Files.createFile(tfPath.resolve("factory3").resolve("directoryWithWrongNameDirectory")
                .resolve("signature.tsv"));

        Files.createDirectory(tfPath.resolve("factory4"));
        Files.createDirectory(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile"));
        Files.createDirectory(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile")
                .resolve("12.dir"));
        Files.createFile(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile")
                .resolve("12.dir").resolve("1.d"));
        Files.createFile(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile")
                .resolve("signature.tsv"));

        Files.createDirectory(tfPath.resolve("factory5"));
        Files.createDirectory(tfPath.resolve("factory5").resolve("directoryWithoutSignature"));
        Files.createDirectory(tfPath.resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir"));
        Files.createFile(tfPath.resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir")
                .resolve("1.dat"));
    }

    @After
    public void providerCreatingPlatformCleaning() throws IOException {
        Path tfPath = tempFolder.getRoot().toPath();

        Files.delete(tfPath.resolve("factory1").resolve("emptyDirectory"));
        Files.delete(tfPath.resolve("factory1"));

        Files.delete(tfPath.resolve("factory2").resolve("directoryWithFile").resolve("signature.tsv"));
        Files.delete(tfPath.resolve("factory2").resolve("directoryWithFile").resolve("someFile"));
        Files.delete(tfPath.resolve("factory2").resolve("directoryWithFile"));
        Files.delete(tfPath.resolve("factory2"));

        Files.delete(tfPath.resolve("factory3").resolve("directoryWithWrongNameDirectory")
                .resolve("signature.tsv"));
        Files.delete(tfPath.resolve("factory3").resolve("directoryWithWrongNameDirectory").resolve("12.dat"));
        Files.delete(tfPath.resolve("factory3").resolve("directoryWithWrongNameDirectory"));
        Files.delete(tfPath.resolve("factory3"));

        Files.delete(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile")
                .resolve("signature.tsv"));
        Files.delete(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile")
                .resolve("12.dir").resolve("1.d"));
        Files.delete(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile")
                .resolve("12.dir"));
        Files.delete(tfPath.resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile"));
        Files.delete(tfPath.resolve("factory4"));

        Files.delete(tfPath.resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir")
                .resolve("1.dat"));
        Files.delete(tfPath.resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir"));
        Files.delete(tfPath.resolve("factory5").resolve("directoryWithoutSignature"));
        Files.delete(tfPath.resolve("factory5"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullProviderCreating() throws IOException {
        factory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyProviderCreating() throws IOException {
        factory.create("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameProviderCreating() throws IOException {
        factory.create("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    //------------------------------------------------------------------------

    @Before
    public void tableGettingCreatingRemovingPlatformMaking() throws IOException {
        provider = factory.create("providerName");
        assertNotNull(provider);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableCreating() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        provider.createTable(null, types);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableCreating() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        provider.createTable("", types);
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableCreating() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        provider.createTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~", types);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTypesCreating() throws IOException {
        provider.createTable("car", null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTypesCreating() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        provider.createTable("child", types);
    }

    @Test (expected = IllegalArgumentException.class)
    public void typesWithNullCreating() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(null);
        types.add(String.class);
        provider.createTable("child", types);
    }

    @Test (expected = IllegalArgumentException.class)
    public void typesWithIncorrectTypeCreating() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Character.class);
        types.add(String.class);
        provider.createTable("child", types);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableRemoving() throws IOException {
        provider.removeTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableRemoving() throws IOException {
        provider.removeTable("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableRemoving() throws IOException {
        provider.removeTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    @Test (expected = IllegalStateException.class)
    public void nonexistingTableRemoving() throws IOException {
        provider.removeTable("notExistingTableName");
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableGetting() {
        provider.getTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableGetting() {
        provider.getTable("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableGetting() {
        provider.getTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    @Test
    public void nonexistingTableGetting() {
        assertNull(provider.getTable("notExistingTableName"));
    }

    @Test
    public void existingTableCreating() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        table = provider.createTable("tableName", types);
        assertNotNull(table);
        assertNull(provider.createTable("tableName", types));
        provider.removeTable("tableName");
    }

    @Test
    public void manyTableGettingCreatingRemoving() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Table table1 = provider.createTable("tableName1", types);
        Table table2 = provider.createTable("tableName2", types);
        Table table3 = provider.createTable("tableName3", types);
        Table table4 = provider.createTable("русскоеНазвание", types);
        assertNotNull(table1);
        assertNotNull(table2);
        assertNotNull(table3);
        assertNotNull(table4);

        assertNotNull(provider.getTable("tableName1"));
        assertNotNull(provider.getTable("tableName2"));
        assertNotNull(provider.getTable("tableName3"));
        assertNotNull(provider.getTable("русскоеНазвание"));

        assertNull(provider.createTable("tableName1", types));
        assertNull(provider.createTable("tableName2", types));
        assertNull(provider.createTable("tableName3", types));
        assertNull(provider.createTable("русскоеНазвание", types));

        assertNotNull(provider.getTable("tableName1"));
        assertNotNull(provider.getTable("tableName2"));
        assertNotNull(provider.getTable("tableName3"));
        assertNotNull(provider.getTable("русскоеНазвание"));

        provider.removeTable("tableName2");
        provider.removeTable("tableName3");

        assertNotNull(provider.getTable("tableName1"));
        assertNull(provider.getTable("tableName2"));
        assertNull(provider.getTable("tableName3"));
        assertNotNull(provider.getTable("русскоеНазвание"));

        Table table5 = provider.createTable("tableName2", types);
        Table table6 = provider.createTable("tableName3", types);
        assertNotNull(table5);
        assertNotNull(table6);

        assertNotNull(provider.getTable("tableName1"));
        assertNotNull(provider.getTable("tableName2"));
        assertNotNull(provider.getTable("tableName3"));
        assertNotNull(provider.getTable("русскоеНазвание"));

        provider.removeTable("tableName1");
        provider.removeTable("tableName2");
        provider.removeTable("tableName3");
        provider.removeTable("русскоеНазвание");

        assertNull(provider.getTable("tableName1"));
        assertNull(provider.getTable("tableName2"));
        assertNull(provider.getTable("tableName3"));
        assertNull(provider.getTable("русскоеНазвание"));
    }

    //------------------------------------------------------------------------

    @Before
    public void tablePlatformMaking() throws IOException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        table = provider.createTable("table", types);
    }

    @After
    public void tablePlatformCleaning() throws IOException {
        provider.removeTable("table");
    }

    @Test
    public void getName() {
        assertEquals("table", table.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullKeyPut() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(666);
        values.add("I'm sexy & i know it!");
        Storeable value = new Storable(types, values);
        table.put(null, value);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyPut() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(666);
        values.add("I'm sexy & i know it!");
        Storeable value = new Storable(types, values);
        table.put("", value);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullValuePut() {
        table.put("key", null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyValuePut() {
        ArrayList<Class<?>> types = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        Storeable value = new Storable(types, values);
        table.put("key", value);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullKeyGet() {
        table.get(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyGet() {
        table.get("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullKeyRemove() {
        table.remove(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyRemove() {
        table.remove("");
    }

    @Test
    public void getPutRemoveSizeTest() {
        ArrayList<Class<?>> types1 = new ArrayList<>();
        types1.add(Integer.class);
        types1.add(String.class);
        ArrayList<Object> values1 = new ArrayList<>();
        values1.add(666);
        values1.add("I'm sexy & i know it!");
        Storeable value1 = new Storable(types1, values1);

        ArrayList<Class<?>> types2 = new ArrayList<>();
        types2.add(Integer.class);
        types2.add(String.class);
        ArrayList<Object> values2 = new ArrayList<>();
        values2.add(666);
        values2.add("I'm sexy & i know it!");
        Storeable value2 = new Storable(types2, values2);

        ArrayList<Class<?>> types3 = new ArrayList<>();
        types3.add(Integer.class);
        types3.add(String.class);
        ArrayList<Object> values3 = new ArrayList<>();
        values3.add(666);
        values3.add("I'm sexy & i know it!");
        Storeable value3 = new Storable(types3, values3);

        assertEquals(0, table.size());

        assertNull(table.get("key1"));
        assertNull(table.get("key2"));
        assertNull(table.get("русскийКлюч"));

        assertEquals(0, table.size());

        assertNull(table.put("key1", value1));
        assertNull(table.put("key2", value2));
        assertNull(table.put("русскийКлюч", value3));

        assertEquals(3, table.size());

        assertEquals(value1, table.get("key1"));
        assertEquals(value2, table.get("key2"));
        assertEquals(value3, table.get("русскийКлюч"));

        assertEquals(3, table.size());

        assertEquals(value2, table.remove("key2"));
        assertNull(table.remove("key2"));

        assertEquals(2, table.size());

        assertEquals(value1, table.get("key1"));
        assertNull(table.get("key2"));
        assertEquals(value3, table.get("русскийКлюч"));
    }

    @Test
    public void commitRollbackTest() throws IOException {
        ArrayList<Class<?>> types1 = new ArrayList<>();
        types1.add(Integer.class);
        types1.add(String.class);
        ArrayList<Object> values1 = new ArrayList<>();
        values1.add(111);
        values1.add("I'm sexy & i know it!");
        Storeable value1 = new Storable(types1, values1);

        ArrayList<Class<?>> types2 = new ArrayList<>();
        types2.add(Integer.class);
        types2.add(String.class);
        ArrayList<Object> values2 = new ArrayList<>();
        values2.add(222);
        values2.add("I'm sexy & i know it!");
        Storeable value2 = new Storable(types2, values2);

        ArrayList<Class<?>> types3 = new ArrayList<>();
        types3.add(Integer.class);
        types3.add(String.class);
        ArrayList<Object> values3 = new ArrayList<>();
        values3.add(333);
        values3.add("I'm sexy & i know it!");
        Storeable value3 = new Storable(types3, values3);

        ArrayList<Class<?>> types4 = new ArrayList<>();
        types4.add(Integer.class);
        types4.add(String.class);
        ArrayList<Object> values4 = new ArrayList<>();
        values4.add(444);
        values4.add("I'm sexy & i know it!");
        Storeable value4 = new Storable(types4, values4);

        assertEquals(0, table.commit());
        table.put("key1", value1);
        table.put("key2", value2);
        assertEquals(2, table.commit());
        table.remove("key1");
        assertEquals(1, table.commit());
        assertEquals(0, table.commit());
        table.put("key2", value4);
        assertEquals(1, table.commit());

        table.put("key3", value3);
        table.remove("key3");
        assertEquals(0, table.commit());

        table.put("key4", value4);
        table.get("key4");
        table.put("key4", value2);
        assertEquals(1, table.commit());
    }

    //------------------------------------------------------------------------

    Storeable store;

    @Before
    public void createStorable() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Long.class);
        types.add(Byte.class);
        types.add(Float.class);
        types.add(Double.class);
        types.add(Boolean.class);
        types.add(String.class);
        store = new Storable(types);
    }

    @Test (expected = ColumnFormatException.class)
    public void createNullTypesStorableTest() {
        Storeable s = new Storable(null);
    }

    @Test (expected = ColumnFormatException.class)
    public void createEmptyTypesStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        Storeable s = new Storable(types);
    }

    @Test (expected = ColumnFormatException.class)
    public void createWrongTypesStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Character.class);
        types.add(String.class);
        Storeable s = new Storable(types);
    }

    @Test (expected = ColumnFormatException.class)
    public void createNullValuesStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Storeable s = new Storable(types, null);
    }

    @Test (expected = ColumnFormatException.class)
    public void createEmptyValuesStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        Storeable s = new Storable(types, values);
    }

    @Test (expected = ColumnFormatException.class)
    public void createWrongValuesStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(111);
        values.add(333);
        Storeable s = new Storable(types, values);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void setWrongIndexStorableTest1() {
        store.setColumnAt(-4, 3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void setWrongIndexStorableTest2() {
        store.setColumnAt(88, "tra-ta-ta");
    }

    @Test (expected = ColumnFormatException.class)
    public void setWrongValueStorableTest() {
        store.setColumnAt(1, 9);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getWrongIndexStorableTest1() {
        store.getColumnAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getWrongIndexStorableTest2() {
        store.getColumnAt(123);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getIntWrongIndexStorableTest1() {
        store.getIntAt(-10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getIntWrongIndexStorableTest2() {
        store.getIntAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getLongWrongIndexStorableTest1() {
        store.getLongAt(-10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getLongWrongIndexStorableTest2() {
        store.getLongAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getByteWrongIndexStorableTest1() {
        store.getByteAt(-10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getByteWrongIndexStorableTest2() {
        store.getByteAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getFloatWrongIndexStorableTest1() {
        store.getFloatAt(-10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getFloatWrongIndexStorableTest2() {
        store.getFloatAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getDoubleWrongIndexStorableTest1() {
        store.getDoubleAt(-10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getDoubleWrongIndexStorableTest2() {
        store.getDoubleAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getBooleanWrongIndexStorableTest1() {
        store.getBooleanAt(-10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getBooleanWrongIndexStorableTest2() {
        store.getBooleanAt(10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getStringWrongIndexStorableTest1() {
        store.getStringAt(-10);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void getStringWrongIndexStorableTest2() {
        store.getStringAt(10);
    }

    @Test (expected = ColumnFormatException.class)
    public void getIntFromAnotherTypeStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(143);
        values.add("luck");
        Storeable s = new Storable(types, values);
        s.getIntAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void getLongFromAnotherTypeStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Long.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(143143143);
        values.add("luck");
        Storeable s = new Storable(types, values);
        s.getLongAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void getByteFromAnotherTypeStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Byte.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(121);
        values.add("luck");
        Storeable s = new Storable(types, values);
        s.getByteAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void getFloatFromAnotherTypeStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Float.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(1.5);
        values.add("luck");
        Storeable s = new Storable(types, values);
        s.getFloatAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void getDoubleFromAnotherTypeStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Double.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(789.123);
        values.add("luck");
        Storeable s = new Storable(types, values);
        s.getDoubleAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void getBooleanFromAnotherTypeStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Boolean.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(true);
        values.add("luck");
        Storeable s = new Storable(types, values);
        s.getBooleanAt(1);
    }

    @Test (expected = ColumnFormatException.class)
    public void getStringFromAnotherTypeStorableTest() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add("THIS IS THE END!!!");
        values.add(777);
        Storeable s = new Storable(types, values);
        s.getStringAt(1);
    }
}
