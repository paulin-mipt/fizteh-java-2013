
package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
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
public class StoreableTableUnitTest {
    TableProvider tableProvider;
    Table testTableEng;
    Table testTableRus;
    List<Class<?>> typesTestListOne;
    List<Class<?>> typesTestListTwo;
    File sandBoxDirectory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUpTestObject() throws IOException {
        sandBoxDirectory = folder.newFolder();
        tableProvider = new StoreableTableProvider(sandBoxDirectory);

        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        testTableEng = tableProvider.createTable("testTable20", typesTestListOne);
        testTableRus = tableProvider.createTable("тестоваяТаблица21", typesTestListTwo);
    }

    @After
    public void tearDownTestObject() throws IOException {
        //tableProvider.removeTable("testTable20");
        //tableProvider.removeTable("тестоваяТаблица21");
        DeleteDirectory.rm(sandBoxDirectory);
    }

    /**
     * TEST BLOCK
     * GET NAME TESTS
     */

    @Test
    public void getNameTest() {
        Assert.assertEquals("testTable20", testTableEng.getName());
        Assert.assertEquals("тестоваяТаблица21", testTableRus.getName());
    }

    /**
     * TEST BLOCK
     * GET TESTS
     */

    @Test
    public void getTest() throws IOException, ParseException {
        testTableEng.put("key", tableProvider.deserialize(testTableEng, "[1, 2, 3]"));
        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTableEng, testTableEng.get("key")));
        Assert.assertNull(testTableEng.get("nonExictingKey"));
        testTableEng.remove("key");

        testTableRus.put("ключ", tableProvider.deserialize(testTableRus, "[1, 2.043, true, \"Hello World!\"]"));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                tableProvider.serialize(testTableRus, testTableRus.get("ключ")));
        Assert.assertNull(testTableRus.get("несуществующийКлюч"));
        testTableRus.remove("ключ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        testTableEng.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTest() {
        testTableEng.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNlTest() {
        testTableEng.get("    ");
    }

    /**
     * TEST BLOCK
     * PUT TESTS
     */

    @Test
    public void putTest() throws IOException, ParseException {
        Assert.assertNull(testTableEng.put("key", tableProvider.deserialize(testTableEng, "[1, 2, 3]")));
        testTableEng.commit();

        ((StoreableTable) testTableEng).close();
        testTableEng = tableProvider.createTable("testTable20", typesTestListOne);

        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTableEng, testTableEng.put("key",
                tableProvider.deserialize(testTableEng, "[1, 2, 3]"))));
        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTableEng, testTableEng.put("key",
                tableProvider.deserialize(testTableEng, "[-1, -2, -3]"))));
        testTableEng.remove("key");

        Assert.assertNull(testTableRus.put("ключ",
                tableProvider.deserialize(testTableRus, "[1, 2.043, true, \"Hello World!\"]")));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                tableProvider.serialize(testTableRus, testTableRus.put("ключ",
                        tableProvider.deserialize(testTableRus, "[1, 2.043, true, \"Hello World!\"]"))));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                tableProvider.serialize(testTableRus, testTableRus.put("ключ",
                        tableProvider.deserialize(testTableRus, "[-1, -2.043, false, \"Bye Bye World!\"]"))));
        testTableRus.remove("ключ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKeyTest() throws IOException, ParseException {
        testTableEng.put(null, testTableEng.put("key", tableProvider.deserialize(testTableEng, "[1, 2, 3]")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullValueTest() {
        testTableEng.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullBothTest() {
        testTableEng.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces1Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = tableProvider.createFor(testTableEng, valuesToPut);
        testTableEng.put("key with whitespaces", needToPut);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces2Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = tableProvider.createFor(testTableEng, valuesToPut);
        testTableEng.put(" key ", needToPut);
    }

    /**
     * TEST BLOCK
     * REMOVE TESTS
     */

    @Test
    public void removeTest() throws IOException, ParseException {
        testTableEng.put("key", tableProvider.deserialize(testTableEng, "[1, 2, 3]"));
        Assert.assertNull(testTableEng.remove("nonExictingKey"));
        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTableEng, testTableEng.remove("key")));

        testTableRus.put("ключ", tableProvider.deserialize(testTableRus, "[1, 2.043, true, \"Hello World!\"]"));
        Assert.assertNull(testTableRus.remove("несуществующийКлюч"));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                tableProvider.serialize(testTableRus, testTableRus.remove("ключ")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        testTableEng.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyTest() {
        testTableEng.remove("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlTest() {
        testTableEng.remove("    ");
    }

    /**
     * TEST BLOCK
     * SIZE TESTS
     */

    @Test
    public void sizeTest() throws IOException, ParseException {
        Assert.assertEquals(0, testTableEng.size());
        testTableEng.put("key1", tableProvider.deserialize(testTableEng, "[1, 2, 3]"));
        Assert.assertEquals(1, testTableEng.size());
        testTableEng.put("key2", tableProvider.deserialize(testTableEng, "[4, 5, 6]"));
        testTableEng.put("key3", tableProvider.deserialize(testTableEng, "[7, 8, 9]"));
        Assert.assertEquals(3, testTableEng.size());
        testTableEng.put("key4", tableProvider.deserialize(testTableEng, "[10, 11, 12]"));
        testTableEng.put("key5", tableProvider.deserialize(testTableEng, "[13, 14, 15]"));
        Assert.assertEquals(5, testTableEng.size());
        testTableEng.commit();
        Assert.assertEquals(5, testTableEng.size());

        for (int i = 1; i <= 5; ++i) {
            testTableEng.remove("key" + i);
        }
    }

    /**
     * TEST BLOCK
     * COMMIT TESTS
     */

    @Test
    public void commitTest() throws IOException, ParseException {
        Assert.assertEquals(0, testTableEng.commit());
        for (int i = 1; i <= 5; ++i) {
            testTableEng.put("key" + i, tableProvider.deserialize(testTableEng, "[1, 2, 3" + i + "]"));
        }
        Assert.assertEquals(5, testTableEng.commit());
        for (int i = 1; i <= 5; ++i) {
            testTableEng.remove("key" + i);
        }
    }

    @Test
    public void commitHardTest() throws IOException, ParseException {
        testTableEng.put("key1", tableProvider.deserialize(testTableEng, "[1, 2, 3]"));
        testTableEng.put("key2", tableProvider.deserialize(testTableEng, "[4, 5, 6]"));
        Assert.assertEquals(2, testTableEng.commit());
        testTableEng.put("key1", tableProvider.deserialize(testTableEng, "[7, 8, 9]"));
        testTableEng.put("key3", tableProvider.deserialize(testTableEng, "[10, 11, 12]"));
        testTableEng.remove("key2");
        Assert.assertEquals(3, testTableEng.commit());
        testTableEng.remove("key1");
        testTableEng.remove("key3");
    }

    /**
     * TEST BLOCK
     * ROLLBACK TESTS
     */

    @Test
    public void rollbackTest() throws IOException, ParseException {
        Assert.assertEquals(0, testTableEng.rollback());
        for (int i = 1; i <= 5; ++i) {
            testTableEng.put("key" + i, tableProvider.deserialize(testTableEng, "[1, 2, 3" + i + "]"));
        }
        testTableEng.commit();
        testTableEng.put("key2", tableProvider.deserialize(testTableEng, "[1, 2, 0]"));
        testTableEng.put("key4", tableProvider.deserialize(testTableEng, "[1, 2, 1]"));
        Assert.assertEquals(2, testTableEng.rollback());
        Assert.assertEquals("[1,2,32]", tableProvider.serialize(testTableEng, testTableEng.get("key2")));
        Assert.assertEquals("[1,2,34]", tableProvider.serialize(testTableEng, testTableEng.get("key4")));
        for (int i = 1; i <= 5; ++i) {
            testTableEng.remove("key" + i);
        }
    }

    @Test
    public void rollbackHardTest() throws IOException, ParseException {
        testTableEng.put("key1", tableProvider.deserialize(testTableEng, "[1, 2, 3]"));
        testTableEng.put("key2", tableProvider.deserialize(testTableEng, "[4, 5, 6]"));
        Assert.assertEquals(2, testTableEng.commit());
        testTableEng.remove("key2");
        testTableEng.put("key2", tableProvider.deserialize(testTableEng, "[4, 5, 6]"));
        Assert.assertEquals(0, testTableEng.rollback());
        testTableEng.remove("key1");
        testTableEng.remove("key2");
    }

    /**
     * TEST BLOCK
     * GET COLUMNS COUNT TESTS
     */

    @Test
    public void getColumnsCountTest() {
        Assert.assertEquals(3, testTableEng.getColumnsCount());
        Assert.assertEquals(4, testTableRus.getColumnsCount());
    }

    /**
     * TEST BLOCK
     * GET COLUMNS TYPE TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnsTypeBadIndex1Test() {
        testTableEng.getColumnType(4);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnsTypeBadIndex2Test() {
        testTableRus.getColumnType(5);
    }

    @Test
    public void getColumnsType1Test() {
        Assert.assertEquals(Integer.class, testTableEng.getColumnType(0));
    }

    @Test
    public void getColumnsType2Test() {
        Assert.assertEquals(Boolean.class, testTableRus.getColumnType(2));
    }

    /**
     * TEST BLOCK
     * CLOSE TESTS
     */

    @Test
    public void doubleCloseTest() throws IOException {
        ((StoreableTable) testTableEng).close();
        ((StoreableTable) testTableEng).close();
        testTableEng = tableProvider.createTable("testTable20", typesTestListOne);
        Assert.assertNotNull(testTableEng);
    }

    @Test(expected = IllegalStateException.class)
    public void getAfterCloseTest() {
        ((StoreableTable) testTableEng).close();
        testTableEng.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void putAfterCloseTest() throws ParseException {
        ((StoreableTable) testTableEng).close();
        testTableEng.put("key", tableProvider.deserialize(testTableEng, "[1, 2, 3]"));
    }

    @Test(expected = IllegalStateException.class)
    public void removeAfterCloseTest() {
        ((StoreableTable) testTableEng).close();
        testTableEng.remove("key");
    }

    @Test(expected = IllegalStateException.class)
    public void getNameAfterCloseTest() {
        ((StoreableTable) testTableEng).close();
        testTableEng.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void rollbackAfterCloseTest() {
        ((StoreableTable) testTableEng).close();
        testTableEng.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void commitAfterCloseTest() throws IOException {
        ((StoreableTable) testTableEng).close();
        testTableEng.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void getColumnTypeAfterCloseTest() {
        ((StoreableTable) testTableEng).close();
        testTableEng.getColumnType(0);
    }

    @Test(expected = IllegalStateException.class)
    public void getColumnsCountAfterCloseTest() {
        ((StoreableTable) testTableEng).close();
        testTableEng.getColumnsCount();
    }

    @Test(expected = IllegalStateException.class)
    public void toStringAfterCloseTest() {
        ((StoreableTable) testTableEng).close();
        testTableEng.toString();
    }

    /**
     * TEST BLOCK
     * TO STRING TESTS
     */

    @Test
    public void toStringTest() {
        Assert.assertEquals("StoreableTable[" + sandBoxDirectory.getAbsolutePath() + "/testTable20]",
                testTableEng.toString());
        Assert.assertEquals("StoreableTable[" + sandBoxDirectory.getAbsolutePath() + "/тестоваяТаблица21]",
                testTableRus.toString());
    }
}
