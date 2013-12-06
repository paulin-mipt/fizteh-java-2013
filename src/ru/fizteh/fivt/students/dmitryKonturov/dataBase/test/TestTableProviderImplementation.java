package ru.fizteh.fivt.students.dmitryKonturov.dataBase.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderFactoryImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderImplementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTableProviderImplementation {
    private TableProviderImplementation provider;
    private File providerWorkspace;
    private File correctTable;
    private List<Class<?>> correctTypeList;
    File correctTableSignatureFile;


    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void initializeTemporaryFolder() throws IOException {
        TableProviderFactoryImplementation factory = new TableProviderFactoryImplementation();
        assertNotNull("Factory should be not null", factory);

        providerWorkspace = tempFolder.newFolder("workspace");

        correctTable = new File(providerWorkspace, "correctTable");
        assertTrue(correctTable.mkdir());

        correctTableSignatureFile = new File(correctTable, "signature.tsv");
        assertTrue(correctTableSignatureFile.createNewFile());
        try (FileWriter sign = new FileWriter(correctTableSignatureFile)) {
            sign.write("int String double boolean byte long float");
        }

        provider = (TableProviderImplementation) factory.create(providerWorkspace.toString());
        assertNotNull("Provider should be not null", provider);

        correctTypeList = new ArrayList<>();
        correctTypeList.add(Integer.class);
        correctTypeList.add(String.class);
        correctTypeList.add(Double.class);
        correctTypeList.add(Boolean.class);
        correctTypeList.add(Byte.class);
        correctTypeList.add(Long.class);
        correctTypeList.add(Float.class);

    }

    //------------------GetName--------------------
    @Test (expected = IllegalArgumentException.class)
    public void testGetTableNameIsNull() {
        provider.getTable(null);
    }

    @Test
    public void testGetTableNotExistingTable() {
        Table table = provider.getTable("notExistingTable");
        assertNull("Not existing table should be null", table);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetTableWrongName() {
        provider.getTable("Very \\.. wrong* /name");
    }

    @Test
    public void testGetTableCorrect() {
        Table table = provider.getTable(correctTable.getName());
        assertNotNull(table);
    }

    @Test
    public void testGetTableDoubleExecution() {
        Table tableFirst = provider.getTable(correctTable.getName());
        Table tableSecond = provider.getTable(correctTable.getName());
        assertSame(tableFirst, tableSecond);
    }

    //-------------------------CreateTable---------------------------------

    @Test (expected = IllegalArgumentException.class)
    public void testCreateTableNameNull() throws IOException {
        provider.createTable(null, correctTypeList);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateTableWrongName() throws IOException {
        provider.createTable("\\*.wrong./", correctTypeList);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateTableIncorrectList() throws IOException {
        List<Class<?>> wrongList = new ArrayList<>();
        wrongList.add(Boolean.class);
        wrongList.add(ArrayList.class);
        provider.createTable("incorrectListTable", wrongList);
    }

    @Test
    public void testCreateTableWithExistsName() throws IOException {
        Table tempTable = provider.createTable(correctTable.getName(), correctTypeList);
        assertNull("Table should be null", tempTable);
    }

    @Test
    public void testCreateTableCorrect() throws IOException {
        Table tempTable = provider.createTable("table3", correctTypeList);
        assertNotNull("Table should not be null", tempTable);
    }

    //-------------------RemoveTable--------------------------------------

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveTableNullName() throws IOException {
        provider.removeTable(null);
    }

    @Test (expected = IllegalStateException.class)
    public void testRemoveTableWithNotExistsName() throws IOException {
        provider.removeTable("tableNotExistingTable");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveTableWithIncorrectName() throws IOException {
        provider.removeTable("deleteInc*rrectN*metab|e");
    }

    @Test
    public void testRemoveCorrectTable() throws IOException {
        provider.removeTable(correctTable.getName());
    }

    //----------------Deserialise-------------------

    @Test (expected = IllegalArgumentException.class)
    public void testDeserializeNullTable() throws ParseException {
        String value = "[\"some string\"]";
        provider.deserialize(null, value);
    }

    @Test (expected = ParseException.class)
    public void testDeserializeWrong() throws IOException, ParseException {
        Table table = provider.getTable(correctTable.getName());
        provider.deserialize(table, "[78.1, true, \"string\", 21, 22, 23, 24]");
    }

    @Test (expected = ParseException.class)
    public void testDeserializeWrongColumnCount() throws IOException, ParseException {
        Table table = provider.getTable(correctTable.getName());
        assertNotNull(table);
        provider.deserialize(table, "[100, 10.1]");
    }

    //-----------------Serialize----------------------------

    @Test (expected = IllegalArgumentException.class)
    public void testSerialiseNullTable() throws IOException {
        List<Object> values = new ArrayList<>();
        values.add(10000);
        values.add("My string");
        values.add(189.19918);
        values.add(true);
        values.add(Byte.valueOf("10", 10));
        values.add(52752855);
        values.add(10.98);
        Table table = provider.getTable(correctTable.getName());
        assertNotNull(table);
        provider.serialize(null, provider.createFor(table, values));
    }

    //--------------CreateFor----------------

    @Test (expected = IllegalArgumentException.class)
    public void testCreateForNullTable() {
        provider.createFor(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateForNullValues() throws IOException {
        Table table = provider.getTable(correctTable.getName());
        provider.createFor(table, null);
    }

    @Test (expected = ColumnFormatException.class)
    public void testCreateForIncorrectValues() throws IOException {
        Table table = provider.getTable(correctTable.getName());
        List<Object> values = new ArrayList<>();
        values.add(10000);
        values.add(false);
        values.add("String lol");
        values.add(true);
        values.add(Byte.valueOf("10", 10));
        values.add(52752855);
        values.add(10.98);
        provider.createFor(table, values);
    }
}
