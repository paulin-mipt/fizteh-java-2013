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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class TestStoreableImplementation {
    private TableImplementation table;
    private TableProviderImplementation provider;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void initTable() throws IOException {
        TableProviderFactoryImplementation factory = new TableProviderFactoryImplementation();
        assertNotNull(factory);

        File providerFile = temporaryFolder.newFolder("providerFolder");
        provider = (TableProviderImplementation) factory.create(providerFile.toString());
        assertNotNull(provider);

        List<Class<?>> typesList = new ArrayList<>();
        typesList.add(Integer.class);
        typesList.add(Long.class);
        typesList.add(Boolean.class);
        typesList.add(Byte.class);
        typesList.add(Float.class);
        typesList.add(Double.class);
        typesList.add(String.class);

        table = (TableImplementation) provider.createTable("correctTableName", typesList);
        assertNotNull(table);
    }

    //---------------------Set------------------------

    @Test (expected = IndexOutOfBoundsException.class)
    public void testSetNegativeindex() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);
        toTest.setColumnAt(-1, null);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testSetOverBound() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);
        toTest.setColumnAt(table.getColumnsCount() + 1, null);
    }

    @Test (expected = ColumnFormatException.class)
    public void testSetColumnFormat() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);
        toTest.setColumnAt(0, "Not an integer");
    }

//------------------Get------------------

    @Test (expected = IndexOutOfBoundsException.class)
    public void testGetNegativeindex() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);
        toTest.getColumnAt(-1);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testGetOverBound() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);
        toTest.getColumnAt(table.getColumnsCount() + 1);
    }

//--------------Set and get------------------

    @Test
    public void testCorrectInt() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);

        Integer toSet = Integer.valueOf(10);
        toTest.setColumnAt(0, toSet);
        assertEquals(toTest.getIntAt(0), toSet);
    }

    @Test
    public void testCorrectLong() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);

        Long toSet = Long.valueOf(800002774);
        toTest.setColumnAt(1, toSet);
        assertEquals(toTest.getLongAt(1), toSet);
    }

    @Test
    public void testCorrectBoolean() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);

        Boolean toSet = Boolean.FALSE;
        toTest.setColumnAt(2, toSet);
        assertEquals(toTest.getBooleanAt(2), toSet);
    }

    @Test
    public void testCorrectByte() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);

        Byte toSet = Byte.valueOf((byte) 15);
        toTest.setColumnAt(3, toSet);
        assertEquals(toTest.getByteAt(3), toSet);
    }

    @Test
    public void testCorrectFloat() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);

        Float toSet = Float.valueOf((float) 67.2);
        toTest.setColumnAt(4, toSet);
        assertEquals(toTest.getFloatAt(4), toSet);
    }

    @Test
    public void testCorrectDouble() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);

        Double toSet = Double.valueOf(100.826);
        toTest.setColumnAt(5, toSet);
        assertEquals(toTest.getDoubleAt(5), toSet);
    }

    @Test
    public void testCorrectString() {
        Storeable toTest = provider.createFor(table);
        assertNotNull(toTest);

        String toSet = "test String";
        toTest.setColumnAt(6, toSet);
        assertEquals(toTest.getStringAt(6), toSet);
    }



}
