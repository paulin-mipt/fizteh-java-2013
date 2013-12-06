package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:42
 */
public class StoreableRowUnitTest {
    TableProvider tableProvider;
    Table testTable;
    Storeable testStoreable;
    Storeable newTestStoreable;
    File sandBoxDirectory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUpTestObject() throws IOException, ParseException {
        sandBoxDirectory = folder.newFolder();

        tableProvider = new StoreableTableProvider(sandBoxDirectory);
        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.
                createListOfTypesFromString("int long byte float double boolean String");
        testTable = tableProvider.createTable("testTable22", typesTestListOne);
        testStoreable = tableProvider.deserialize(testTable,
                "[0, 3000000000, 0, 0.123, 1.7976931348623157E308, true, null]");
    }

    @After
    public void tearDownTestObject() throws IOException {
        tableProvider.removeTable("testTable22");
        DeleteDirectory.rm(sandBoxDirectory);
    }

    /**
     * TEST BLOCK
     * SET COLUMN AT TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnAtBadIndexTest() {
        testStoreable.setColumnAt(10, "wrong index");
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnAtWrongTypeTest() {
        testStoreable.setColumnAt(0, 2.043);
    }

    @Test
    public void setColumnAtTest() {
        testStoreable.setColumnAt(0, 2);
        Assert.assertEquals(new Integer(2), testStoreable.getIntAt(0));
    }

    /**
     * TEST BLOCK
     * GET COLUMN AT TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnAtBadIndexTest() {
        testStoreable.getColumnAt(10);
    }

    @Test
    public void getColumnAtTest() {
        Assert.assertEquals(true, testStoreable.getColumnAt(5));
    }

    /**
     * TEST BLOCK
     * GET INT AT TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntAtBadIndexTest() {
        testStoreable.getIntAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void getIntAtWrongTypeTest() {
        testStoreable.getIntAt(3);
    }

    @Test
    public void getIntAtTest() {
        Assert.assertEquals(new Integer(0), testStoreable.getIntAt(0));
    }

    /**
     * TEST BLOCK
     * GET LONG AT TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongAtBadIndexTest() {
        testStoreable.getLongAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void getLongAtWrongTypeTest() {
        testStoreable.getLongAt(3);
    }

    @Test
    public void getLongAtTest() {
        Assert.assertEquals(new Long("3000000000"), testStoreable.getLongAt(1));
    }

    /**
     * TEST BLOCK
     * GET BYTE AT TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteAtBadIndexTest() {
        testStoreable.getByteAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void getByteAtWrongTypeTest() {
        testStoreable.getByteAt(3);
    }

    @Test
    public void getByteAtTest() {
        Assert.assertEquals(new Byte("0"), testStoreable.getByteAt(2));
    }

    /**
     * TEST BLOCK
     * GET FLOAT AT TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatAtBadIndexTest() {
        testStoreable.getFloatAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void getFloatAtWrongTypeTest() {
        testStoreable.getFloatAt(4);
    }

    @Test
    public void getFloatAtTest() {
        Assert.assertEquals(new Float(0.123), testStoreable.getFloatAt(3));
    }

    /**
     * TEST BLOCK
     * GET DOUBLE TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleAtBadIndexTest() {
        testStoreable.getDoubleAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void getDoubleAtWrongTypeTest() {
        testStoreable.getDoubleAt(3);
    }

    @Test
    public void getDoubleAtTest() {
        Assert.assertEquals(new Double(1.7976931348623157E308), testStoreable.getDoubleAt(4));
    }

    /**
     * TEST BLOCK
     * GET BOOLEAN TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanAtBadIndexTest() {
        testStoreable.getBooleanAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void getBooleanAtWrongTypeTest() {
        testStoreable.getBooleanAt(3);
    }

    @Test
    public void getBooleanAtTest() {
        Assert.assertEquals(true, testStoreable.getBooleanAt(5));
    }

    /**
     * TEST BLOCK
     * GET STRING TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringAtBadIndexTest() {
        testStoreable.getStringAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void getStringAtWrongTypeTest() {
        testStoreable.getStringAt(3);
    }

    @Test
    public void getStringAtTest() {
        Assert.assertEquals(null, testStoreable.getStringAt(6));
    }

    /**
     * TEST BLOCK
     * TO STRING TESTS
     */

    @Test
    public void toStringTest() {
        Assert.assertEquals("StoreableRow[0,3000000000,0,0.123,1.7976931348623157E308,true,]",
                testStoreable.toString());
    }
}
