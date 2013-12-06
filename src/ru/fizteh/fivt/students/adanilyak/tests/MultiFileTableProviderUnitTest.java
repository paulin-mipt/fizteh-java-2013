package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileTableProvider;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 28.10.13
 * Time: 0:11
 */
public class MultiFileTableProviderUnitTest {
    MultiFileTableProvider testProvider;

    @Before
    public void setUpTestObject() throws IOException {
        testProvider = new MultiFileTableProvider(new File("/Users/Alexander/Documents/JavaDataBase/Tests"));
    }

    /**
     * TEST BLOCK
     * CREATE TABLE TESTS
     */

    @Test
    public void createTableTest() {
        Assert.assertNotNull(testProvider.createTable("testTable1"));
        Assert.assertNull(testProvider.createTable("testTable1"));

        Assert.assertNotNull(testProvider.createTable("тестоваяТаблица2"));
        Assert.assertNull(testProvider.createTable("тестоваяТаблица2"));

        testProvider.removeTable("testTable1");
        testProvider.removeTable("тестоваяТаблица2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableTest() {
        testProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyTableTest() {
        testProvider.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNlTableTest() {
        testProvider.createTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadNameTableTest() {
        testProvider.createTable("not_normal-name@for$table^!");
    }

    /**
     * TEST BLOCK
     * GET TABLE TESTS
     */

    @Test
    public void getTableTest() {
        Assert.assertNull(testProvider.getTable("testNonExictingTable3"));
        Assert.assertNull(testProvider.getTable("тестоваяНесуществующаяТаблица4"));

        testProvider.createTable("testTable5");
        Assert.assertNotNull(testProvider.getTable("testTable5"));

        testProvider.createTable("тестоваяТаблица6");
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
    public void removeTableTest() {
        testProvider.createTable("testTable7");
        testProvider.createTable("тестоваяТаблица8");

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
}
