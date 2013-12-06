package ru.fizteh.fivt.students.fedoseev.multifilehashmap.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTable;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTableProvider;

public class MultiFileHashMapTableTest {
    private static MultiFileHashMapTableProvider tp;
    private MultiFileHashMapTable table;

    public MultiFileHashMapTableTest() {
        tp = new MultiFileHashMapTableProvider();
        table = tp.createTable("test");
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("test", table.getName());
    }

    @Test
    public void testGet() throws Exception {
        table.put("England", "forEnglish");
        Assert.assertEquals("forEnglish", table.get("England"));

        table.put("Россия", "дляРусских");
        Assert.assertEquals("дляРусских", table.get("Россия"));

        table.put("日本", "日本人のための");
        Assert.assertEquals("日本人のための", table.get("日本"));
    }

    @Test
    public void testPut() throws Exception {
        Assert.assertNull(table.put("rhinoceros", "hippopotamus"));
    }

    @Test
    public void testRemove() throws Exception {
        table.put("burrito", "chimichanga");
        Assert.assertEquals("chimichanga", table.remove("burrito"));
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void testPutSize() throws Exception {
        table.put("damn", "itAll");
        Assert.assertEquals(1, table.size());
    }

    @Test
    public void testCommit() throws Exception {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void testPutCommit() throws Exception {
        table.put("cymkih,mergeMeCompletely", "please");
        Assert.assertEquals(1, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(0, table.rollback());
    }

    @Test
    public void testPutRollback() throws Exception {
        table.put("NO", "motherf*cker");
        Assert.assertEquals(1, table.rollback());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() throws Exception {
        table.get("  \t  \n");
    }

    @Test
    public void testGetNotExisting() throws Exception {
        Assert.assertNull(table.get("devastation"));
    }

    @Test
    public void testPutOverwriteGet() throws Exception {
        table.put("Hey!What`sUp,dawg?", "BogOff!");
        table.put("Hey!What`sUp,dawg?", "Cool,Bro!");
        Assert.assertEquals("Cool,Bro!", table.get("Hey!What`sUp,dawg?"));
    }

    @Test
    public void testRemoveGet() throws Exception {
        table.put("favoloso", "benissimo");
        table.remove("favoloso");
        Assert.assertNull(table.get("favoloso"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        table.put("smth", null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put(null, "smth");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws Exception {
        table.put("  \t  \n", "smth");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() throws Exception {
        table.put("smth", "  \t  \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() throws Exception {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmpty() throws Exception {
        table.put("  \t  \n", "  \t  \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullEmpty() throws Exception {
        table.put(null, "  \t  \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyNull() throws Exception {
        table.put("  \t  \n", null);
    }

    @Test
    public void testPutOverwrite() throws Exception {
        table.put("Dolphins", "areStupid");
        Assert.assertEquals("areStupid", table.put("Dolphins", "areSmart"));
    }

    @Test
    public void testPutCommitGet() throws Exception {
        table.put("quéGuay", "fenomenal");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals("fenomenal", table.get("quéGuay"));
    }

    @Test
    public void testPutRollbackGet() throws Exception {
        table.put("quéGuay", "fenomenal");
        Assert.assertEquals(1, table.rollback());
        Assert.assertNull(table.get("quéGuay"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() throws Exception {
        table.remove("  \t  \n");
    }

    @Test
    public void testRemoveNotExisting() throws Exception {
        Assert.assertNull(table.remove("devastation"));
    }

    @Test
    public void testPutRemoveCommitGet() throws Exception {
        table.put("crap", "awesomeness");
        Assert.assertEquals("awesomeness", table.remove("crap"));
        Assert.assertEquals(0, table.commit());
        Assert.assertNull(table.get("crap"));
    }

    @Test
    public void testPutCommitRemoveRollbackGet() throws Exception {
        table.put("crap", "awesomeness");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals("awesomeness", table.remove("crap"));
        Assert.assertEquals(1, table.rollback());
        Assert.assertEquals("awesomeness", table.get("crap"));
    }

    @Test
    public void testPutRemoveCommitGetSize() throws Exception {
        table.put("blah", "bang");
        Assert.assertEquals(1, table.size());
        Assert.assertEquals("bang", table.remove("blah"));
        Assert.assertEquals(0, table.commit());
        Assert.assertEquals(0, table.size());
        Assert.assertNull(table.get("blah"));
    }

    @Test
    public void testPutCommitRemoveRollbackGetSize() throws Exception {
        table.put("blah", "bang");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals(1, table.size());
        Assert.assertEquals("bang", table.remove("blah"));
        Assert.assertEquals(1, table.rollback());
        Assert.assertEquals(1, table.size());
        Assert.assertEquals("bang", table.get("blah"));
    }

    @Test
    public void testPutSizeCommitRollback() throws Exception {
        table.put("楽しんで", "瞬間");
        Assert.assertEquals(1, table.size());

        table.put("真個", "うれしさ");
        Assert.assertEquals(2, table.size());

        table.put("楽しんで", "瞬間");
        Assert.assertEquals(2, table.commit());
        Assert.assertEquals(2, table.size());

        table.remove("楽しんで");
        Assert.assertEquals(1, table.size());

        table.remove("真個");
        Assert.assertEquals(0, table.size());

        Assert.assertEquals(2, table.rollback());
        Assert.assertEquals(0, table.commit());
        Assert.assertEquals(2, table.size());
    }

    @After
    public void tearDown() {
        tp.removeTable("test");
    }
}
