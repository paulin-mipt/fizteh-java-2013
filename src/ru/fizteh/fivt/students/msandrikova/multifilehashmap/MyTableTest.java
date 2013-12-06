package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTableTest {
    private MyTable table;
    private File path;
    
    @After
    public void clear() {
        if (path.exists()) {
            try {
                Utils.remover(path, "test", false);
            } catch (Exception e) {
                System.err.println("Can not remove something");
            }
        }
    }
    
    @Before
    public void setUp() throws Exception {
        path = new File(System.getProperty("user.home"), "sandbox");
        clear();
        new File(path, "tableName").mkdirs();
        table = new MyTable(path, "tableName");
    }

    @Test
    public void testGetName() {
        assertEquals(table.getName(), "tableName");
    }

    @Test
    public void testPutGetOverwrite() {
        assertNull(table.put("key", "value"));
        assertEquals(table.get("key"), "value");
        assertEquals(table.put("key", "newvalue"), "value");
        assertEquals(table.get("key"), "newvalue");
    }

    @Test
    public void testRemove() {
        assertNull(table.put("key", "value"));
        assertEquals(table.remove("key"), "value");
        assertNull(table.get("key"));
    }

    @Test
    public void testSize() {
        assertNull(table.put("key", "value"));
        assertEquals(table.size(), 1);
        assertEquals(table.put("key", "newvalue"), "value");
        assertEquals(table.remove("key"), "newvalue");
        assertEquals(table.size(), 0);
    }

    @Test
    public void testCommitRollback() {
        assertNull(table.put("key", "value"));
        assertEquals(table.commit(), 1);
        assertEquals(table.remove("key"), "value");
        assertEquals(table.put("key", "value"), null);
        assertEquals(table.rollback(), 0);
        assertNull(table.put("stuff", "stuff"));
        assertEquals(table.rollback(), 1);
        assertNull(table.get("stuff"));
    }
}
