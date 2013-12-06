package ru.fizteh.fivt.students.elenav.multifilemap.tests;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapProvider;

public class MultiFileMapTest {
    
    private final String key = "key";
    private final String value = "value";
    private TableProvider provider;
    private Table table;
    
    @Before
    public void init() {
        provider = new MultiFileMapProvider(new File("d:/javaTest"), System.out);
        table = provider.createTable("First");
    }
    
    @After
    public void clear() {
        provider.removeTable("First");
    }
    
    @Test
    public void testGetName() {
        Assert.assertEquals("First", table.getName());
    }
    
    @Test
    public void testSize() {
        for (int i = 0; i < 3; ++i) {
            table.put(key + i, value + i);
        }
        Assert.assertEquals(3, table.size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        table.get(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() {
        table.put(null, value);
        table.put(key, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        table.remove(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutEmpty() {
        table.put("", value);
        table.put(key, "");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() {
        table.get("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() {
        table.remove("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutSpaces() {
        table.put(" ", value);
        table.put(key, "   ");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetSpaces() {
        table.get("  ");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSpaces() {
        table.remove("  ");
    }
    
    @Test
    public void testGet() {
        table.put(key, value);
        Assert.assertEquals(value, table.get(key));
        Assert.assertNull(table.get(value));
    }
    
    @Test
    public void testPut() {
        Assert.assertNull(table.put(key, value));
        Assert.assertEquals(value, table.put(key, value));
    }
    
    @Test
    public void testRemove() {
        table.put(key, value);
        Assert.assertNull(table.remove(value));
        Assert.assertEquals(value, table.remove(key));
    }
    
    @Test
    public void testCommit() {
        table.put(key, value);
        table.put(key + 1, value + 1);
        table.put(key, value);
        Assert.assertEquals(2, table.commit());
    }
    
    @Test
    public void testRollback() {
        for (int i = 0; i < 3; ++i) {
            table.put(key + i, value + i);
        }
        table.commit();
        table.put(key + 10, value + 10);
        table.put(key + 1, value + 1);
        Assert.assertEquals(1, table.rollback());
        Assert.assertNull(table.get(key + 10));
        Assert.assertEquals(value + 2, table.get(key + 2));
    }
    
}
