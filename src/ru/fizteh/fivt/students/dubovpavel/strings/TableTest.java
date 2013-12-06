package ru.fizteh.fivt.students.dubovpavel.strings;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

import java.io.File;

public class TableTest {
    private StringWrappedMindfulDataBaseMultiFileHashMap db;
    private Dispatcher dispatcher;
    private File path;

    private void cleanRecursively(File pointer) {
        if (pointer.isDirectory()) {
            for (File sub : pointer.listFiles()) {
                cleanRecursively(sub);
            }
        }
        assert (pointer.delete());
    }

    @Before
    public void setUp() {
        String homeDir = System.getProperty("user.home");
        path = new File(homeDir, "sandbox/strings");
        cleanRecursively(path);
        assert (path.mkdirs());
        dispatcher = new Dispatcher(false);
        File tablePath = new File(path, "tableName");
        assert (tablePath.mkdir());
        db = new StringWrappedMindfulDataBaseMultiFileHashMap(tablePath, dispatcher);
    }

    @After
    public void tearDown() {
        cleanRecursively(path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        db.get(null);
    }

    @Test
    public void testPutGet() {
        db.put("key", "value");
        Assert.assertEquals("value", db.get("key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        db.put("key", null);
    }

    @Test
    public void testRemove() {
        db.put("key", "value");
        Assert.assertEquals("value", db.remove("key"));
        Assert.assertEquals(null, db.get("key"));
    }

    @Test
    public void testCommit() {
        db.put("key", "value");
        db.put("dummy", "stuff");
        db.remove("dummy");
        Assert.assertEquals(db.commit(), 1);
        Assert.assertEquals(db.get("dummy"), null);
        Assert.assertEquals(db.get("key"), "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        db.remove(null);
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("tableName", db.getName());
    }

    @Test
    public void testRollback() {
        db.put("key", "value");
        Assert.assertEquals(db.rollback(), 1);
        Assert.assertEquals(db.get("key"), null);
    }

    @Test
    public void testCommitRollbackEmpty() {
        Assert.assertEquals(db.commit(), 0);
        Assert.assertEquals(db.rollback(), 0);
    }
}
