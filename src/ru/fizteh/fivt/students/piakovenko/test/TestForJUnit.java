package ru.fizteh.fivt.students.piakovenko.test;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 24.11.13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.piakovenko.filemap.strings.DataBasesFactory;

import java.util.Random;

public class TestForJUnit {
    private static final int KEYS_COUNT = 20;
    private static final String TEST_TABLE_NAME = "test";

    TableProviderFactory factory = new DataBasesFactory();
    TableProvider provider = factory.create("C:\\temp\\JUnit");
    Table table;

    @Before
    public void initialize() throws Exception {
        table = provider.createTable(TEST_TABLE_NAME);
        initializeTable();
    }

    private void initializeTable() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            table.put(key, value);
        }
    }

    @After
    public void afterTest() throws Exception {
        provider.removeTable(TEST_TABLE_NAME);
    }

    @Test
    public void tableExistingData() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String expectedValue = String.format("value%d", index);
            String key = String.format("key%d", index);
            Assert.assertEquals(expectedValue, table.get(key));
        }
    }

    @Test
    public void tableNonExistingData() {
        Random random = new Random();
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("k%d", random.nextInt(100));
            Assert.assertNull(table.get(key));
        }
    }

    @Test
    public void tableNewData() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String newKey = String.format("new_key%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertNull(table.put(newKey, newValue));
        }
    }

    @Test
    public void tableReplaceData() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String oldValue = String.format("value%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertEquals(oldValue, table.put(key, newValue));
        }
    }

    @Test
    public void tableCommit() {
        int committed = table.commit();
        Assert.assertEquals(KEYS_COUNT, committed);
        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            table.put(key, value);
        }
        Assert.assertEquals(KEYS_COUNT, table.commit());
        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            Assert.assertNotNull(table.get(key));
        }
    }

    @Test
    public void tableRollback() {
        Assert.assertEquals(KEYS_COUNT, table.rollback());

        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            table.put(key, value);
        }
        Assert.assertEquals(2 * KEYS_COUNT, table.rollback());
        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            Assert.assertNull(table.get(key));
        }
    }

    @Test
    public void tableSize() {
        Assert.assertEquals(KEYS_COUNT, table.size());
    }

    @Test
    public void tableGetName() {
        Assert.assertEquals(TEST_TABLE_NAME, table.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tableExceptions() {
        table.get(null);
        table.put(null, "value");
        table.put("key", null);
        table.remove(null);
    }

    @Test
    public void rollbackCommit() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            table.put(key, value);
        }
        table.commit();
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            table.remove(key);
        }
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            table.put(key, value);
        }
        Assert.assertEquals(0, table.rollback());

        table.remove("non-exists");
        table.remove("non-exists1");
        table.remove("key1");
        table.put("key1", "value1");
        Assert.assertEquals(0, table.rollback());

        table.put("key1", "value1");
        table.commit();
        table.remove("key1");
        table.put("key1", "value1");
        Assert.assertEquals(0, table.rollback());
    }

}

