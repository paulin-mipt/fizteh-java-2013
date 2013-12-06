package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TableTest {

    private static Table table;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() {
        File rootDir;
        TableProviderFactory factory;
        TableProvider prov;
        try {
            rootDir = folder.newFolder("myroot");
            factory = new TableProviderFactory();
            prov = factory.create(rootDir.getAbsolutePath());
            table = prov.createTable("newTable");
        } catch (IOException e) {
            System.err.println("can't make tests");
        }
    }

    @Test
    public void testGetName() {
        assertNotNull(table.getName());
        assertEquals(table.getName(), "newTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        table.put(null, "lalala");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmpty() {
        table.put("", "lalala");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNl() {
        table.put("                     ", "lalala");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullVal() {
        table.put("alala", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() {
        table.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNl() {
        table.get("                 ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() {
        table.remove("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNl() {
        table.remove("              ");
    }

    @Test
    public void testPutGetRemove() {
        assertNull(table.put("key", "value"));
        assertNotNull(table.put("key", "value2"));
        //assertNotEquals(table.get("key"), "value");
        assertEquals(table.put("key", "value"), "value2");
        assertNull(table.get("other_key"));
        assertEquals(table.remove("key"), "value");
        assertNull(table.get("key"));
    }

    @Test
    public void testPutGetRemoveCyrillic() {
        assertNull(table.put("ключ", "значение1"));
        assertNotNull(table.put("ключ", "значение2"));
        //assertNotEquals(table.get("ключ"), "значение");
        assertEquals(table.put("ключ", "ЗнАчЕниЕ3"), "значение2");
        assertNull(table.get("другой_ключ"));
        assertEquals(table.remove("ключ"), "ЗнАчЕниЕ3");
        assertNull(table.get("ключ"));
    }

    @Test
    public void testSizeCommitRollback() {
        int sz = 442;
        for (int i = 0; i < sz; i++) {
            table.put(Integer.toString(i), Integer.toString(i + 1));
        }
        assertEquals(table.size(), sz);
        assertEquals(table.commit(), sz);
        assertEquals(table.size(), sz);
        assertEquals(table.rollback(), 0);
        for (int i = 0; i < sz; i++) {
            assertEquals(table.remove(Integer.toString(i)), Integer.toString(i + 1));
        }
        assertEquals(table.size(), 0);
        assertEquals(table.rollback(), sz);
    }

    @Test
    public void testRollback() {
        table.put("11", "2");
        assertEquals(table.commit(), 1);
        assertEquals(table.remove("11"), "2");
        assertEquals(table.rollback(), 1);
        assertEquals(table.size(), 1);

        assertEquals(table.put("11", "3"), "2");
        table.put("11", "2");
        assertEquals(table.rollback(), 0);

        table.remove("11");
        assertEquals(table.rollback(), 1);
        assertEquals(table.get("11"), "2");

        table.put("key", "value");
        table.commit();
        table.put("key", "value222");
        table.put("key", "value2323");
        table.put("key", "value");
        assertEquals(table.rollback(), 0);

        table.put("blabla", "uuu1");
        table.remove("blabla");
        table.put("blabla", "uuu2");
        table.remove("blabla");
        table.put("blabla", "uuu3");
        table.remove("blabla");
        assertEquals(table.rollback(), 0);
    }

    @Test
    public void testCommit() {
        table.put("11", "2");
        assertEquals(table.commit(), 1);

        assertEquals("2", table.put("11", "3"));
        table.put("k", "2");
        assertEquals(2, table.commit());

        table.put("k", "2");
        table.put("k", "3");
        table.put("k", "2");
        assertEquals(0, table.commit());

        table.remove("11");
        table.remove("k");
        assertEquals(2, table.commit());
        assertEquals(0, table.size());

        table.put("blabla", "uuu1");
        table.remove("blabla");
        table.put("blabla", "uuu2");
        table.remove("blabla");
        table.put("blabla", "uuu3");
        table.remove("blabla");
        assertEquals(0, table.commit());

    }

}
