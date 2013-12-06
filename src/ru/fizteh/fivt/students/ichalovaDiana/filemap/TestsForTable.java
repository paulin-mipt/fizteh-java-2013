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

public class TestsForTable {
    
    TableProviderFactoryImplementation tableProviderFactory;
    TableProviderImplementation tableProvider;
    TableImplementation table;
    
    Storeable value1;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void createTable() throws IOException {
        File databaseDirectory = folder.newFolder("database");
        tableProviderFactory = new TableProviderFactoryImplementation();
        tableProvider = (TableProviderImplementation) tableProviderFactory.create(databaseDirectory.toString());
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        table = (TableImplementation) tableProvider.createTable("tableName", columnTypes);
        
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, true);
        value1.setColumnAt(1, "AA");
        value1.setColumnAt(2, 5);
    }
    
    @Test
    public void getName() throws Exception {
        Assert.assertEquals("tableName", table.getName());
    }
    
    @Test(expected = IllegalStateException.class)
    public void tableProviderFactoryCloseAndCreate() throws Exception {
        tableProviderFactory.close();
        tableProviderFactory.create("smth");
    }
    
    @Test
    public void tableProviderFactoryDoubleClose() throws Exception {
        tableProviderFactory.close();
        tableProviderFactory.close();
    }
    
    @Test(expected = IllegalStateException.class)
    public void tableProviderFactoryCloseTableProviderGetTable() throws Exception {
        tableProviderFactory.close();
        tableProvider.getTable("tableName");
    }
    
    @Test(expected = IllegalStateException.class)
    public void tableProviderCloseTableProviderGetTable() throws Exception {
        tableProvider.close();
        tableProvider.getTable("tableName");
    }
    
    @Test
    public void tableProviderDoubleClose() throws Exception {
        tableProvider.close();
        tableProvider.close();
    }
    
    @Test(expected = IllegalStateException.class)
    public void tableProviderCloseTableCommit() throws Exception {
        tableProvider.close();
        table.commit();
    }
    
    @Test(expected = IllegalStateException.class)
    public void tableCloseTableCommit() throws Exception {
        table.close();
        table.commit();
    }
    
    @Test
    public void tableDoubleClose() throws Exception {
        table.close();
        table.close();
    }
    
    @Test
    public void getNonExistingKey() {
        Assert.assertNull(table.get("not-exists"));
    }
    
    @Test
    public void putPutSameKey() {
        Assert.assertNull(table.put("key1", value1));
        Assert.assertEquals(value1, table.put("key1", value1));
    }
    
    @Test
    public void putGetRemoveGet() {
        Assert.assertNull(table.put("key1", value1));
        Assert.assertEquals(value1, table.get("key1"));
        Assert.assertEquals(value1, table.remove("key1"));
        Assert.assertNull(table.get("key1"));
    }
    
    @Test
    public void removeNonExistingKey() {
        Assert.assertNull(table.get("not-exists"));
    }
    
    @Test
    public void onePutSize() {
        Assert.assertNull(table.put("key1", value1));
        Assert.assertTrue(table.size() == 1);
    }
    
    @Test
    public void putPutSameKeySize() {
        
        Assert.assertNull(table.put("key1", value1));
        Assert.assertEquals(value1, table.put("key1", value1));
        Assert.assertTrue(table.size() == 1);
    }
    
    @Test
    public void putRollbackGet() {
        Assert.assertNull(table.put("key1", value1));
        Assert.assertTrue(table.rollback() == 1);
        Assert.assertNull(table.get("key1"));
    }
    
    @Test
    public void putCommitRollbackGet() throws IOException {
        Assert.assertNull(table.put("key1", value1));
        Assert.assertTrue(table.commit() == 1);
        Assert.assertTrue(table.rollback() == 0);
        Assert.assertTrue(((TableImplementation) table).storeableAreEqual(value1, table.get("key1")));
    }
    
    @Test
    public void putRemoveCommit() throws IOException {
        Assert.assertNull(table.put("key1", value1));
        Assert.assertEquals(value1, table.remove("key1"));
        Assert.assertTrue(table.commit() == 0);
        Assert.assertTrue(table.rollback() == 0);
    }
    
    @Test
    public void putCommitPutSameValueCommit() throws IOException {
        Assert.assertNull(table.put("key1", value1));
        Assert.assertTrue(table.commit() == 1);
        Assert.assertTrue(((TableImplementation) table).storeableAreEqual(value1, table.put("key1", value1)));
        Assert.assertTrue(table.commit() == 0);
    }
    
    @Test
    public void putputSameKey() throws IOException {
        Assert.assertNull(table.put("key1", value1));
        Storeable value2 = tableProvider.createFor(table);
        value2.setColumnAt(0, false);
        value2.setColumnAt(1, "BB");
        value2.setColumnAt(2, 6);
        Assert.assertTrue(((TableImplementation) table).storeableAreEqual(value1, table.put("key1", value2)));
    }
    
}
