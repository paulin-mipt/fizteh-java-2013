package ru.fizteh.fivt.students.vlmazlov.storeable.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableTest {
    private StoreableTable table;
    private StoreableTableProvider provider;
    private Storeable val1;
    private Storeable val2;
    private Storeable val3;
    private Storeable val4;
    private final String root = "StoreableTableTest";

    @Before
    public void setUp() {
        try {
            File tempDir = FileUtils.createTempDir(root, null);
            provider = new StoreableTableProvider(tempDir.getPath(), false);

            List<Object> values1 = new ArrayList<Object>() { {
                add(null);
                add(new String("val1"));
                add(Byte.valueOf("-3"));
            }};

            List<Object> values2 = new ArrayList<Object>() { {
                add(125);
                add(new String("val2"));
                add(Byte.valueOf("-50"));
            }};

            List<Object> values3 = new ArrayList<Object>() { {
                add(1255);
                add(new String("val3"));
                add(Byte.valueOf("50"));
            }};

            List<Object> values4 = new ArrayList<Object>() { {
                add(12555);
                add(new String("val4"));
                add(null);
            }};

            List<Class<?>> valueTypes = new ArrayList<Class<?>>() { {
                add(Integer.class);
                add(String.class);
                add(Byte.class);
            }};

            table = provider.createTable("testTable", valueTypes);
            val1 = provider.createFor(table, values1);
            val2 = provider.createFor(table, values2);
            val3 = provider.createFor(table, values3);
            val4 = provider.createFor(table, values4);

        } catch (ValidityCheckFailedException ex) {
            Assert.fail("validity check failed: " + ex.getMessage());
        } catch (IOException ex) {
            Assert.fail("Input/output error: check failed: " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        if (provider.getTable("testTable") != null) {
            provider.removeTable("testTable");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void puttingNullValueShouldFail() {
        table.put("key1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void puttingNullKeyShouldFail() {
        table.put(null, val1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingNullShouldFail() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removingNullShouldFail() {
        table.remove(null);
    }

    @Test
    public void commitDiffCountTest() {
        table.put("key1", val1);
        table.put("key1", val2);
        table.remove("key1");
        table.put("key1", val3);
        try {
            Assert.assertEquals("there is only one diff", 1, table.commit());
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Test
    public void rollbackTest() {
        table.put("key1", val1);
        table.put("key2", val2);

        try {
            table.commit();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        table.put("key3", val3);
        table.rollback();

        Assert.assertNull("rollback didn't reverse putting key5", table.get("key3"));
    }

    @Test
    public void getRemovedTest() {
        table.put("key1", val1);
        table.remove("key1");

        Assert.assertNull("key1 wasn't removed", table.get("key1"));
    }

    @Test
    public void getValidTest() {
        table.put("key1", val1);
        table.put("key2", val2);
        table.remove("key1");

        Assert.assertNotNull("key2 wasn't found", table.get("key2"));
    }

    @Test
    public void putOverwriteTest() {
        table.put("key1", val1);
        table.put("key1", val2);

        Assert.assertEquals("value wasn't overwritten",
                val2, table.get("key1"));
    }

    @Test
    public void putAddTest() {
        table.put("key1", val1);
        table.put("key2", val2);

        Assert.assertEquals("value wasn't stored",
                val2, table.get("key2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespacesShouldFail1() {
        table.put("   k e w   ee  ", val1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespacesShouldFail2() {
        table.put(" kewee", val1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespacesShouldFail3() {
        table.put("kewee ", val1);
    }

    @Test
    public void removeTest() {
        table.put("key1", val1);
        table.put("key2", val2);
        table.put("key1", val3);
        table.put("key2", val4);
        table.remove("key2");

        Assert.assertNull("value wasn't removed", table.get("key2"));
    }

    @Test
    public void toStringTest() {
        Assert.assertEquals("wrong string representation",
                "StoreableTable" + "[" + new File(provider.getRoot(), "testTable").getPath() + "]",
                table.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void putAfterClose() {
        table.put("key1", val1);
        table.close();
        table.put("key2", val2);
    }

    @Test
    public void nameIsCorrect() {
        Assert.assertEquals("Incorrect table name", "testTable", table.getName());
    }

    @Test
    public void sizeIsCorrect() {
        table.put("key1", val1);
        table.put("key2", val2);
        table.put("key3", val3);
        table.put("key4", val4);
        Assert.assertEquals("Incorrect size", 4, table.size());
    }

    @Test
    public void removeFourCommitTest() throws IOException {
        table.put("key1", val1);
        table.put("key2", val2);
        table.put("key3", val3);
        table.put("key4", val4);
        Assert.assertEquals("Incorrect diff on put commit", 4, table.commit());   
        table.close();
        table = provider.getTable("testTable");
        table.remove("key1");
        table.remove("key2"); 
        table.remove("key3"); 
        table.remove("key4");
        Assert.assertEquals("Incorrect diff on remove commit", 4, table.commit());
    }

    @Test
    public void columnTypeIsCorrect() {
        Assert.assertEquals("Incorrect columnt type", Byte.class, table.getColumnType(2));
    }

    @Test
    public void columnsCountIsCorrect() {
        Assert.assertEquals("Incorrect columns count", 3, table.getColumnsCount());
    }

    private int readSizeTsv() throws IOException {

        File sizeTsv = new File(new File(provider.getRoot(), table.getName()), "size.tsv");

        Scanner scanner = new Scanner(sizeTsv);

        try {
            return scanner.nextInt();
        } finally {
            scanner.close();
        }
    }

    @Test
    public void putCommitSizeTsv() throws IOException {
        table.put("key2", val2);

        int size = table.size();

        table.commit();

        int sizeTsv = readSizeTsv();
        
        Assert.assertEquals("Size from size.tsv should be equal size from table.size()", size, sizeTsv);

        // Это обычный put
        table.put("key1", val1);

        // Это обычный коммит
        table.commit();

        Assert.assertEquals("size.tsv should be bigger after commit", sizeTsv + 1, readSizeTsv());

    }

    //IndexOutOfBounds tests

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeColumnType() {
        table.getColumnType(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void tooLargeIndexColumnType() {
        table.getColumnType(3);
    }
} 
