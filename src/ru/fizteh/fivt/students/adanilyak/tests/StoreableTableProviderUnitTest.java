package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;
import ru.fizteh.fivt.students.adanilyak.tools.CountingTools;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:42
 */
public class StoreableTableProviderUnitTest {
    StoreableTableProvider testProvider;
    File sandBoxDirectory;
    List<Class<?>> typesTestListOne;
    List<Class<?>> typesTestListTwo;
    List<Class<?>> typesTestListThree;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUpTestObject() throws IOException {
        sandBoxDirectory = folder.newFolder();
        testProvider = new StoreableTableProvider(sandBoxDirectory);
    }

    @After
    public void tearDownTestObject() throws IOException {
        DeleteDirectory.rm(sandBoxDirectory);
    }

    /**
     * TEST BLOCK
     * CREATE TABLE TESTS
     */

    @Test
    public void createTableTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        Assert.assertNotNull(testProvider.createTable("testTable1", typesTestListOne));
        Assert.assertNull(testProvider.createTable("testTable1", typesTestListOne));

        Assert.assertNotNull(testProvider.createTable("тестоваяТаблица2", typesTestListTwo));
        Assert.assertNull(testProvider.createTable("тестоваяТаблица2", typesTestListTwo));

        testProvider.removeTable("testTable1");
        testProvider.removeTable("тестоваяТаблица2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableNotNullListTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        testProvider.createTable(null, typesTestListOne);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNotNullTableNullListTest() throws IOException {
        testProvider.createTable("testTable9", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableNullListTest() throws IOException {
        testProvider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyTableTest() throws IOException {
        typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");
        testProvider.createTable("", typesTestListTwo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNlTableTest() throws IOException {
        typesTestListThree = WorkWithStoreableDataBase.createListOfTypesFromString("String boolean");
        testProvider.createTable("    ", typesTestListThree);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadNameTableTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        testProvider.createTable("not_normal-name@for$table^!", typesTestListOne);
    }

    /**
     * TEST BLOCK
     * GET TABLE TESTS
     */

    @Test
    public void getTableTest() throws IOException {
        Assert.assertNull(testProvider.getTable("testNonExictingTable3"));
        Assert.assertNull(testProvider.getTable("тестоваяНесуществующаяТаблица4"));

        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        testProvider.createTable("testTable5", typesTestListOne);
        Assert.assertNotNull(testProvider.getTable("testTable5"));

        testProvider.createTable("тестоваяТаблица6", typesTestListTwo);
        Assert.assertNotNull(testProvider.getTable("тестоваяТаблица6"));

        testProvider.removeTable("testTable5");
        testProvider.removeTable("тестоваяТаблица6");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTableTest() {
        testProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTableTest() {
        testProvider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNlTableTest() {
        testProvider.getTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBadNameTableTest() {
        testProvider.getTable("not_normal-name@for$table^!");
    }

    /**
     * TEST BLOCK
     * REMOVE TABLE TESTS
     */

    @Test
    public void removeTableTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        testProvider.createTable("testTable7", typesTestListOne);
        testProvider.createTable("тестоваяТаблица8", typesTestListTwo);

        testProvider.removeTable("testTable7");
        Assert.assertNull(testProvider.getTable("testTable7"));

        testProvider.removeTable("тестоваяТаблица8");
        Assert.assertNull(testProvider.getTable("тестоваяТаблица8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTableTest() {
        testProvider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyTableTest() {
        testProvider.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlTableTest() {
        testProvider.removeTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeBadNameTableTest() {
        testProvider.removeTable("not_normal-name@for$table^!");
    }

    @Test(expected = IllegalStateException.class)
    public void removeNonExcitingTable() {
        testProvider.removeTable("testNonExcitingTable11");
    }

    /**
     * TEST BLOCK
     * DESERIALIZE TESTS
     */

    @Test(expected = ParseException.class)
    public void deserializeNullStringTest() throws IOException, ParseException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable10", typesTestListOne);
        testProvider.deserialize(testTable, null);
    }

    @Test(expected = ParseException.class)
    public void deserializeEmptyStringTest() throws IOException, ParseException {
        typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");
        Table testTable = testProvider.createTable("testTable11", typesTestListTwo);
        testProvider.deserialize(testTable, "");
    }

    @Test
    public void deserializeStringJSON1Test() throws IOException, ParseException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable12", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(2);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable = testProvider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue(CountingTools.equals(testTable,
                testProvider.deserialize(testTable, "[1, 2, 3]"), testStoreable));
    }

    @Test
    public void deserializeStringWithNullFieldJSON2Test() throws IOException, ParseException {
        typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");
        Table testTable = testProvider.createTable("testTable13", typesTestListTwo);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(2.0432);
        valuesToMakeStoreable.add(false);
        valuesToMakeStoreable.add(null);
        Storeable testStoreable = testProvider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue(CountingTools.equals(testTable, testProvider.
                deserialize(testTable, "[1,2.0432,false,null]"), testStoreable));
    }

    /**
     * TEST BLOCK
     * SERIALIZE TESTS
     */

    @Test(expected = ColumnFormatException.class)
    public void serializeNullStorableTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable14", typesTestListOne);
        testProvider.serialize(testTable, null);
    }

    @Test
    public void serializeEmptyStorableTest() throws IOException {
        typesTestListTwo = WorkWithStoreableDataBase.
                createListOfTypesFromString("int double boolean String");
        Table testTable = testProvider.createTable("testTable15", typesTestListTwo);
        Storeable testStorable = testProvider.createFor(testTable);
        Assert.assertTrue((testProvider.serialize(testTable, testStorable)).
                equals("[null,null,null,null]"));
    }

    @Test
    public void serializeMixedStorableTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable16", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStorable = testProvider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue((testProvider.serialize(testTable, testStorable)).equals("[1,null,3]"));
    }

    /**
     * TEST BLOCK
     * CREATE FOR (1) TESTS
     */

    @Test
    public void createForWithOneParameterTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable17", typesTestListOne);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = testProvider.createFor(testTable));
        Assert.assertNull(testStoreable.getColumnAt(1));
    }

    /**
     * TEST BLOCK
     * CREATE FOR (2) TESTS
     */

    @Test
    public void createForWithTwoParametersTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable18", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = testProvider.createFor(testTable, valuesToMakeStoreable));
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            columnTypes.add(testTable.getColumnType(i));
        }
        Assert.assertTrue(CheckOnCorrect.goodStoreable(testStoreable, columnTypes));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void createForWithTwoParametersBadObjectsListTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable19", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        testProvider.createFor(testTable, valuesToMakeStoreable);
    }

    @Test(expected = ColumnFormatException.class)
    public void createForWithTwoParametersBadColumnTypesTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable19", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(2.043);
        testProvider.createFor(testTable, valuesToMakeStoreable);
    }

    /**
     * TEST BLOCK
     * CLOSE TESTS
     */

    @Test(expected = IllegalStateException.class)
    public void toStringAfterCloseTest() {
        testProvider.close();
        testProvider.toString();
    }

    @Test(expected = IllegalStateException.class)
    public void getNonExistingTableAfterCloseTest() {
        testProvider.close();
        testProvider.getTable("notExistingTable");
    }

    @Test
    public void openTableUsingGetAfterClosedTable() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        StoreableTable testTable = (StoreableTable) testProvider.createTable("testTable1", typesTestListOne);
        testTable.close();
        StoreableTable testTableNewReference;
        Assert.assertNotNull(testTableNewReference = (StoreableTable) testProvider.getTable("testTable1"));
        Assert.assertEquals("testTable1", testTableNewReference.getName());
        Assert.assertNotEquals(testTable, testTableNewReference);
    }

    @Test(expected = IllegalStateException.class)
    public void createTableAfterCloseTest() throws IOException {
        testProvider.close();
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        testProvider.createTable("newExistingTable", typesTestListOne);
    }

    @Test
    public void openTableUsingCreateAfterCloseTest() throws IOException {
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        StoreableTable testTable = (StoreableTable) testProvider.createTable("testTable1", typesTestListOne);
        testTable.close();
        StoreableTable testTableNewReference;
        Assert.assertNotNull(testTableNewReference =
                (StoreableTable) testProvider.createTable("testTable1", typesTestListOne));
        Assert.assertEquals("testTable1", testTableNewReference.getName());
        Assert.assertNotEquals(testTable, testTableNewReference);
    }

    @Test(expected = IllegalStateException.class)
    public void removeTableAfterCloseTest() throws IOException {
        testProvider.close();
        testProvider.removeTable("testTable1");
    }

    @Test(expected = IllegalStateException.class)
    public void deserializeTableAfterCloseTest() throws ParseException, IOException {
        testProvider.close();
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable12", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(2);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable = testProvider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue(CountingTools.equals(testTable,
                testProvider.deserialize(testTable, "[1, 2, 3]"), testStoreable));
    }


    @Test(expected = IllegalStateException.class)
    public void serializeTableAfterCloseTest() throws ParseException, IOException {
        testProvider.close();
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable16", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStorable = testProvider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue((testProvider.serialize(testTable, testStorable)).equals("[1,null,3]"));
    }

    @Test(expected = IllegalStateException.class)
    public void createForTableAfterCloseTest1() throws ParseException, IOException {
        testProvider.close();
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable17", typesTestListOne);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = testProvider.createFor(testTable));
        Assert.assertNull(testStoreable.getColumnAt(1));
    }

    @Test(expected = IllegalStateException.class)
    public void createForTableAfterCloseTest2() throws IOException {
        testProvider.close();
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        Table testTable = testProvider.createTable("testTable18", typesTestListOne);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = testProvider.createFor(testTable, valuesToMakeStoreable));
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            columnTypes.add(testTable.getColumnType(i));
        }
        Assert.assertTrue(CheckOnCorrect.goodStoreable(testStoreable, columnTypes));
    }

    /**
     * TEST BLOCK
     * TO STRING TESTS
     */

    @Test
    public void toStringTest() {
        Assert.assertEquals("StoreableTableProvider[" + sandBoxDirectory.getAbsolutePath() + "]",
                testProvider.toString());
    }
}
