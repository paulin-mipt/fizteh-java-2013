package ru.fizteh.fivt.students.inaumov.multifilemap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.base.DatabaseFactory;

import java.util.Random;

public class MultiFileTableTest {
    private static final int KEYS_COUNT = 20;
    private static final String TABLE_NAME = "test";

    TableProviderFactory tableProviderFactory = new DatabaseFactory();
    TableProvider tableProvider = tableProviderFactory.create("/database_test");

    Table currentTable;

    public void prepare() {
        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            String value = "value" + i;
            currentTable.put(key, value);
        }
    }

    @Before
    public void setup() {
        currentTable = tableProvider.createTable(TABLE_NAME);
        prepare();
    }

    @After
    public void after() {
        tableProvider.removeTable(TABLE_NAME);
    }

    @Test
    public void testTableGetExistingData() {
        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            String expectedValue = "value" + i;
            Assert.assertEquals(expectedValue, currentTable.get(key));
        }
    }

    @Test
    public void testTableGetNonExistingData() {
        Random random = new Random();
        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "xxkey" + i;
            Assert.assertNull(currentTable.get(key));
        }
    }

    @Test
    public void testTablePutNewData() {
        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "new_key" + i;
            String value = "new_value" + i;
            Assert.assertNull(currentTable.put(key, value));
        }
    }

    @Test
    public void testTableReplaceData() {
        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            String newValue = "new_value" + i;
            String oldValue = "value" + i;
            Assert.assertEquals(oldValue, currentTable.put(key, newValue));
        }
    }

    @Test
    public void testTableCommit() {
        Assert.assertEquals(KEYS_COUNT, currentTable.commit());

        for (int i = 0; i < 2 * KEYS_COUNT; ++i) {
            String key = "key" + i;
            String value = "value" + i;
            currentTable.put(key, value);
        }

        Assert.assertEquals(KEYS_COUNT, currentTable.commit());

        for (int i = 0; i < 2 * KEYS_COUNT; ++i) {
            String key = "key" + i;
            Assert.assertNotNull(currentTable.get(key));
        }
    }

    @Test
    public void testTableRollback() {
        Assert.assertEquals(KEYS_COUNT, currentTable.rollback());

        for (int i = 0; i < 2 * KEYS_COUNT; ++i) {
            String key = "key" + i;
            String value = "value" + i;
            currentTable.put(key, value);
        }

        Assert.assertEquals(2 * KEYS_COUNT, currentTable.rollback());

        for (int i = 0; i < 2 * KEYS_COUNT; ++i) {
            String key = "key" + i;
            Assert.assertNull(currentTable.get(key));
        }
    }

    @Test
    public void testTableSize() {
        Assert.assertEquals(KEYS_COUNT, currentTable.size());
    }

    @Test
    public void testTableGetName() {
        Assert.assertEquals(TABLE_NAME, currentTable.getName());
    }

    @Test
    public void testTableRollbackCommit() {
        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            String value = "value" + i;
            currentTable.put(key, value);
        }

        Assert.assertEquals(20, currentTable.commit());

        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            currentTable.remove(key);
        }

        for (int i = 0; i < KEYS_COUNT; ++i) {
            String key = "key" + i;
            String value = "value" + i;
            currentTable.put(key, value);
        }

        Assert.assertEquals(0, currentTable.rollback());

        currentTable.remove("non-ex0");
        currentTable.remove("non-ex1");

        currentTable.remove("key1");

        currentTable.put("key1", "value1");

        Assert.assertEquals(0, currentTable.rollback());

        currentTable.put("key1", "value1");
        currentTable.commit();

        currentTable.remove("key1");
        currentTable.put("key1", "value1");
        Assert.assertEquals(0, currentTable.rollback());
    }
}
