package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.junit;

import java.io.File;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MyTableProviderFactory;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class TableProviderTest {
    
    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private TableProvider provider;

    @Before
    public void setUp() throws Exception {
        new File(DATA_BASE_DIR).mkdirs();
        provider = new MyTableProviderFactory().create(DATA_BASE_DIR);
    }
    
    @After
    public void tearDown() {
        File f = new File(DATA_BASE_DIR);
        for (File s: f.listFiles()) {
            CommandRemove.deleteRecursivly(s);
        }
    }

    @Test
    public void testCreateTable() throws Exception {
        Assert.assertNotNull(provider.createTable("createTable"));
        Assert.assertNull(provider.createTable("createTable"));
        provider.removeTable("createTable");
        Assert.assertNotNull(provider.createTable("createTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIllegalSymbols() throws Exception {
        provider.createTable("%:^*(&^i");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableIllegalSymbols() throws Exception {
        provider.getTable("O*&^*");
    }

    @Test
    public void testGetCreateTableReference() throws Exception {
        Table table = provider.createTable("getCreateTableReference");
        Assert.assertNotNull(table);
        Assert.assertNotNull(provider.getTable("getCreateTableReference"));
        Assert.assertSame(provider.getTable("getCreateTableReference"), table);
        Assert.assertSame(provider.getTable("getCreateTableReference"), 
                          provider.getTable("getCreateTableReference"));
        provider.removeTable("getCreateTableReference");
    }

    @Test
    public void testGetNonExistingTable() {
        Assert.assertNull(provider.getTable("nonExictingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() throws Exception {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
    }
    
    
}
