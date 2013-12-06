package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TestsForTableProvider {
    
    TableProviderFactory tableProviderFactory;
    TableProvider tableProvider;
    Table table;
    List<Class<?>> columnTypes;
    
    Storeable value1;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void createTable() throws IOException {
        File databaseDirectory = folder.newFolder("database");
        tableProviderFactory = new TableProviderFactoryImplementation();
        tableProvider = tableProviderFactory.create(databaseDirectory.toString());
        
        columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getTableNullTableName() {
        tableProvider.getTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getTableNullByteTableName() {
        tableProvider.getTable("a\0b");
    }
    
    public void getNonExistingTable() {
        Assert.assertNull(tableProvider.getTable("not-exists"));
    }
    
    @Test
    public void getTableForSameNamesShouldReturnSameObject() throws IOException {
        tableProvider.createTable("temp", columnTypes);
        Assert.assertEquals(tableProvider.getTable("temp"), tableProvider.getTable("temp"));
        tableProvider.removeTable("temp");
    }
    
    
    @Test
    public void createTableForExistingTableReturnsNull() throws IOException {
        tableProvider.createTable("temp", columnTypes);
        Assert.assertNull(tableProvider.createTable("temp", columnTypes));
        tableProvider.removeTable("temp");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createTableNullTableName() throws IOException {
        tableProvider.createTable(null, columnTypes);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createTableNullByteTableName() throws IOException {
        tableProvider.createTable("a\0b", columnTypes);
    }
    
    
    @Test(expected = IllegalStateException.class)
    public void removeNonExistingTable() throws IOException {
        tableProvider.removeTable("not-exists");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeTableNullTableName() throws IOException {
        tableProvider.removeTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeTableNullByteTableName() throws IOException {
        tableProvider.removeTable("a\0b");
    }
       
    @Test 
    public void createRemove() throws IOException {
        tableProvider.createTable("temp", columnTypes);
        Assert.assertNotNull(tableProvider.getTable("temp"));
        tableProvider.removeTable("temp");
        Assert.assertNull(tableProvider.getTable("temp"));
    }
}
