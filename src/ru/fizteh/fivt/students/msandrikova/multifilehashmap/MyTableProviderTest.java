package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTableProviderTest {
    private MyTableProvider tableProvider;
    private File path;

    @After
    public void clear() throws Exception {
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
        path.mkdirs();
        tableProvider = new MyTableProvider(path);
    }

    
    @Test
    public void testGetTable() {
        assertNull(tableProvider.getTable("tableName"));
        ChangesCountingTable table = tableProvider.createTable("tableName");
        assertNotNull(tableProvider.getTable("tableName"));
        assertSame(table, tableProvider.getTable("tableName"));
    }

    @Test
    public void testCreateTable() {
        assertNotNull(tableProvider.createTable("tableName"));
        assertNull(tableProvider.createTable("tableName"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistTable() {
        tableProvider.removeTable("NotExist");
    }
    
    @Test
    public void testRemoveTable() {
        tableProvider.createTable("tableName");
        tableProvider.removeTable("tableName");
        assertNull(tableProvider.getTable("tableName"));
    }

}
