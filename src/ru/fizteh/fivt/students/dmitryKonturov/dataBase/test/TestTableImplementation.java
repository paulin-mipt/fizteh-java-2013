package ru.fizteh.fivt.students.dmitryKonturov.dataBase.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderFactoryImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderImplementation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestTableImplementation {
    private TableProviderImplementation provider;
    private TableImplementation correctTable;
    private List<Class<?>> correctTypeList;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void initializeTemporaryFolder() throws IOException {

        TableProviderFactoryImplementation factory = new TableProviderFactoryImplementation();
        assertNotNull("Factory should be not null", factory);

        File providerWorkspace = tempFolder.newFolder("workspace");

        provider = (TableProviderImplementation) factory.create(providerWorkspace.toString());
        assertNotNull(provider);

        correctTypeList = new ArrayList<>();
        correctTypeList.add(String.class);
        correctTypeList.add(Integer.class);
        correctTypeList.add(Double.class);

        correctTable = (TableImplementation) provider.createTable("correctTableName", correctTypeList);
        assertNotNull(correctTable);
    }

    public void fillCorrectTable() {
        Random random = new Random();
        List<Object> valueList = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            valueList.clear();
            valueList.add(String.format("value #N %d", i));
            valueList.add(random.nextInt());
            valueList.add(random.nextDouble());
            Storeable value = provider.createFor(correctTable, valueList);
            String key = String.format("KeyN%d", i);
            correctTable.put(key, value);
        }
    }

//-----------------GetName--------------

    @Test
    public void testGetName() {
        assertEquals("correctTableName", correctTable.getName());
    }

//-----------------Get------------------------

    @Test (expected = IllegalArgumentException.class)
    public void testGetNullKey() {
        correctTable.get(null);
    }

    @Test
    public void testGetNotExistingValue() {
        Storeable value = correctTable.get("NotExists");
        assertNull(value);
    }

    @Test
    public void testPutGet() {
        List<Object> valueList = new ArrayList<>();
        valueList.add("get after put");
        valueList.add(78);
        valueList.add(-89.827);
        Storeable value = provider.createFor(correctTable, valueList);
        correctTable.put("someKey", value);
        Storeable returned = correctTable.get("someKey");
        String first = provider.serialize(correctTable, value);
        String second = provider.serialize(correctTable, returned);

        assertEquals(first, second);
    }

//--------------------Put---------------

    @Test (expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        List<Object> valueList = new ArrayList<>();
        valueList.add("get after put");
        valueList.add(78);
        valueList.add(-89.827);
        Storeable value = provider.createFor(correctTable, valueList);
        correctTable.put(null, value);
    }

    @Test (expected = ColumnFormatException.class)
    public void testPutWrongColumns() {
        List<Object> valueList = new ArrayList<>();
        valueList.add("get after put");
        valueList.add("Not an integer");
        valueList.add(-89.827);
        Storeable value = provider.createFor(correctTable, valueList);
        correctTable.put("WrongColumns", value);
    }

    @Test
    public void testPutCorrect() {
        List<Object> valueList = new ArrayList<>();
        valueList.add("get after put");
        valueList.add(78);
        valueList.add(-89.827);
        Storeable value = provider.createFor(correctTable, valueList);
        correctTable.put("correctKey", value);
    }

 //-----------------Remove---------------------

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNullKey() {
        correctTable.remove(null);
    }

 //---------------------Other----------------------
    @Test
    public void testSize() {
        fillCorrectTable();
        assertEquals(correctTable.size(), 10);
    }

    @Test
    public void testRollBack() {
        fillCorrectTable();
        correctTable.rollback();
        assertEquals(correctTable.size(), 0);
    }

    @Test
    public void testCommit() throws IOException {
        fillCorrectTable();
        correctTable.commit();
        assertEquals(correctTable.size(), 10);

        assertEquals(correctTable.rollback(), 0);
    }

    @Test
    public void testColumnsCount() {
        assertEquals(correctTable.getColumnsCount(), 3);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testColumnsOutBounds() {
        correctTable.getColumnType(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testColumns() {
        correctTable.getColumnType(3);
    }

}
