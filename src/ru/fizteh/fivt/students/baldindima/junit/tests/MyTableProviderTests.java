package ru.fizteh.fivt.students.baldindima.junit.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.baldindima.junit.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderTests {
    static TableProviderFactory factory;
    static TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void before() throws IOException{
        provider = factory.create(folder.newFolder().getCanonicalPath());
        Assert.assertNotNull(provider);
    }

    

    @Test(expected = IllegalStateException.class)
    public void testRemoveNonExistingTable() {
        provider.removeTable("non_existing_table");
    }

    @Test(expected = RuntimeException.class)
    public void testGetTableWithWrongName() {
        provider.getTable(".." + File.separator + "database");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableWithWrongName() {
        provider.createTable(".." + File.separator + "database");
    }

    @Test(expected = RuntimeException.class)
    public void testUseTableWithWrongName() {
        provider.removeTable(".." + File.separator + "database");
    }

    @Test
    public void testGetTable() {
        provider.createTable("test_get_table");
        Assert.assertNotNull(provider.getTable("test_get_table"));
        provider.removeTable("test_get_table");
    }

    @Test
    public void testGetNonExistingTable() {
        Assert.assertNull(provider.getTable("non_existing_table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithNull(){
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNull() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithNull(){
        provider.removeTable(null);
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithEmptyName() {
        provider.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithEmptyName() {
        provider.getTable("");
    }

    
}
