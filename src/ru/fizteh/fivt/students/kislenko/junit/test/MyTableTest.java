package ru.fizteh.fivt.students.kislenko.junit.test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.multifilemap.MyTable;

public class MyTableTest {
    MyTable table;

    @Before
    public void setUp() throws Exception {
        table = new MyTable("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        table.put(null, "null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put("null", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutIncorrectKey() throws Exception {
        table.put("     ", "42");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutIncorrectValue() throws Exception {
        table.put("java", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncorrectKey() throws Exception {
        table.get("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveIncorrectKey() throws Exception {
        table.remove("      ");
    }

    @Test
    public void testPutSimple() throws Exception {
        Assert.assertNull(table.put("a", "b"));
    }

    @Test
    public void testPutOverwrite() throws Exception {
        table.put("a", "b");
        Assert.assertEquals("b", table.put("a", "c"));
    }

    @Test
    public void testRemoveNotExistingKey() throws Exception {
        Assert.assertNull(table.remove("someKey"));
    }

    @Test
    public void testRemoveSimple() throws Exception {
        table.put("a", "b");
        Assert.assertEquals("b", table.remove("a"));
    }

    @Test
    public void testGetNotExistingKey() throws Exception {
        Assert.assertNull(table.get("nothing"));
    }

    @Test
    public void testGetSimple() throws Exception {
        table.put("a", "b");
        Assert.assertEquals("b", table.get("a"));
        table.put("РусскиеБуковкиТожеПоддерживаются", "ДаДа");
        Assert.assertEquals("ДаДа", table.get("РусскиеБуковкиТожеПоддерживаются"));
    }

    @Test
    public void testGetOverwritten() throws Exception {
        table.put("a", "b");
        table.put("a", "c");
        Assert.assertEquals("c", table.get("a"));
    }

    @Test
    public void testGetRemoved() throws Exception {
        table.put("a", "b");
        table.put("c", "d");
        Assert.assertEquals("d", table.get("c"));
        table.remove("c");
        Assert.assertNull(table.get("c"));
    }

    @Test
    public void testCommit() throws Exception {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(0, table.rollback());
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void testPutRollbackGet() throws Exception {
        table.put("useless", "void");
        table.rollback();
        Assert.assertNull(table.get("useless"));
    }

    @Test
    public void testPutCommitGet() throws Exception {
        table.put("a", "b");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals("b", table.get("a"));
    }

    @Test
    public void testPutCommitRemoveRollbackGet() throws Exception {
        table.put("useful", "somethingImportant");
        table.commit();
        table.remove("useful");
        table.rollback();
        Assert.assertEquals("somethingImportant", table.get("useful"));
    }

    @Test
    public void testPutRemoveSize() throws Exception {
        table.put("a", "b");
        table.put("b", "c");
        table.remove("c");
        Assert.assertEquals(2, table.size());
        table.remove("b");
        Assert.assertEquals(1, table.size());
    }

    @Test
    public void testPutCommitRollbackSize() throws Exception {
        table.put("a", "b");
        table.put("b", "c");
        table.put("b", "c");
        Assert.assertEquals(2, table.commit());
        Assert.assertEquals(2, table.size());
        table.remove("b");
        table.remove("a");
        Assert.assertEquals(0, table.size());
        Assert.assertEquals(2, table.rollback());
        Assert.assertEquals(2, table.size());
    }
}
