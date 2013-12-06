package ru.fizteh.fivt.students.baldindima.junit.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.baldindima.junit.MyTableProviderFactory;

import java.io.IOException;


public class MyTableTests {
    static Table table;
    static TableProviderFactory factory;
    static TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void beforeTest() throws IOException {
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());
        table = provider.createTable("new");
    }

    @After
    public void afterTest() {
        provider.removeTable("new");
    }


    @Test
    public void testGetName() {
        Assert.assertEquals(table.getName(), "new");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() {
        table.put(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        table.remove(null);
    }

   

   
    

    @Test
    public void testRollback() {
        Assert.assertEquals(table.size(), 0);
       
        for (int i = 0; i < 100; ++i) {
            Assert.assertNull(table.put(Integer.toString(i), "rollback test"));
        }
        Assert.assertEquals(table.rollback(), 100);
    }

    @Test
    public void testRollbackWithNoChanges() {
        Assert.assertNull(table.put("no_changes", "will_be_deleted_soon"));
        Assert.assertEquals(table.remove("no_changes"), "will_be_deleted_soon");
        Assert.assertEquals(table.rollback(), 0);

        Assert.assertNull(table.put("key", "value"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("key", "value_new"), "value");
        Assert.assertEquals(table.put("key", "value"), "value_new");
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void testCommitWithNoChanges() {
        Assert.assertNull(table.put("no_changes", "will_be_deleted_soon"));
        Assert.assertEquals(table.remove("no_changes"), "will_be_deleted_soon");
        Assert.assertEquals(table.commit(), 0);

        Assert.assertNull(table.put("key", "value"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("key", "value_new"), "value");
        Assert.assertEquals(table.put("key", "value"), "value_new");
        Assert.assertEquals(table.commit(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() {
        table.put("   ", "empty_key");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyValue() {
        table.put("empty_value", "   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValue() {
        table.put("null_value", null);
    }

    @Test
    public void testWorkWithTable() {
        Assert.assertNull(table.put("work", "with"));
        Assert.assertEquals(table.get("work"), "with");
        Assert.assertEquals(table.remove("work"), "with");
        Assert.assertEquals(table.remove("work"), null);
        Assert.assertEquals(table.commit(), 0);
        Assert.assertEquals(table.rollback(), 0);
        Assert.assertEquals(table.size(), 0);
    }

    @Test
    public void testCommitRollback() {
        Assert.assertNull(table.put("a", "b"));
        Assert.assertEquals(table.get("a"), "b");
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertNull(table.get("a"));
        Assert.assertNull(table.put("a", "b"));
        Assert.assertEquals(table.get("a"), "b");
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.remove("a"), "b");
        Assert.assertNull(table.put("a", "bb"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.get("a"), "bb");
    }
}