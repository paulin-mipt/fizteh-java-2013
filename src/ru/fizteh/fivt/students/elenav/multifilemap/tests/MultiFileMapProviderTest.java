package ru.fizteh.fivt.students.elenav.multifilemap.tests;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.elenav.multifilemap.*;

public class MultiFileMapProviderTest {
    
    private TableProvider provider;
    
    @Before
    public void init() {
        provider = new MultiFileMapProvider(new File("d:/javaTest"), System.out);
    }
    
    @Test
    public void testCreateTable() {
        Assert.assertNotNull(provider.createTable("First"));
        Assert.assertNull(provider.createTable("First"));
        provider.removeTable("First");
    }
    
    @Test
    public void testGetTable() {
        provider.createTable("First");
        Assert.assertNotNull(provider.getTable("First"));
        provider.removeTable("First");
    }
    
    @Test 
    public void testGetTableThatNotExists() {
        Assert.assertNull(provider.getTable("First"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() {
        provider.getTable(null);
    }
    
    @Test 
    public void testGetTableIsCorrect() {
        Table table = provider.createTable("First");
        Assert.assertSame(table, provider.getTable("First"));
        Assert.assertSame(provider.getTable("First"), provider.getTable("First"));
        provider.removeTable("First");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveTableThatNotExists() {
        provider.removeTable("First");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() {
        provider.removeTable(null);
    }
    
    @Test(expected = RuntimeException.class)
    public void testGetBadSymbolTable() {
        provider.getTable(".. ");
    }
    
}
