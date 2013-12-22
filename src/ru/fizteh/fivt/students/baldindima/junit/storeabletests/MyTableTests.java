package ru.fizteh.fivt.students.baldindima.junit.storeabletests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.baldindima.junit.MyTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyTableTests {
    static Table table;
    static TableProviderFactory factory;
    static TableProvider provider;
    static List<Class<?>> types;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void beforeTest() throws IOException {
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());
        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        table = provider.createTable("simple", types);

    }

    public void storeableEquals(Storeable a, Storeable b) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Assert.assertEquals(a.getColumnAt(i),b.getColumnAt(i));
        }
    }

    @Test
    public void testPutGet() throws IOException {
        types = new ArrayList<>();
        types.add(Long.class);
        types.add(Long.class);

        Table table = provider.createTable("simple1", types);

        Storeable storeable = provider.createFor(table);

        storeable.setColumnAt(0, (long) 123123123);
        storeable.setColumnAt(1, (long) 100);

        Assert.assertNull(table.put("simple", storeable));
        storeableEquals(table.get("simple"), storeable);
    }

    @Test
    public void testGetPutSimple() throws IOException {
        Storeable storeable = provider.createFor(table);

       storeable.setColumnAt(0, "new_value");
        storeable.setColumnAt(1, 100);

        Storeable old = provider.createFor(table);
        old.setColumnAt(0, "new_value");
        old.setColumnAt(1, 100);

        Assert.assertNull(table.put("simple", storeable));
        storeableEquals(table.get("simple"), storeable);
        
        Assert.assertEquals(table.commit(), 1);
        storeable.setColumnAt(0, "very_new");
        storeable.setColumnAt(1, null);
        Assert.assertNull(table.put("null", storeable));
        storeableEquals(table.get("null"), storeable);

        storeableEquals(table.remove("null"), storeable);
        System.out.println(table.rollback());
        storeableEquals(table.put("simple", storeable), old);
        
        Assert.assertEquals(table.rollback(), 1);

        storeableEquals(table.remove("simple"), old);
        Assert.assertEquals(table.commit(), 1);

        Assert.assertNull(table.put("key", old));
        Assert.assertEquals(table.commit(), 1);

        storeableEquals(table.put("key", old), old);
        Assert.assertEquals(table.commit(), 0);
    }

    
    
    

    @Test (expected = IllegalArgumentException.class)
    public void testPutSpasedKey() {
        Storeable storeable = provider.createFor(table);

        storeable.setColumnAt(0, "n e w_ v a lu e");
        storeable.setColumnAt(1, 100);

        table.put("  ", storeable);
    }

    
    @Test(expected = ColumnFormatException.class)
    public void testStoreableHasMoreLength() throws IOException {
        List<Class<?>> types1 = new ArrayList<>();
        types1.add(Long.class);
        types1.add(Boolean.class);

        Table table1 = provider.createTable("super1", types1);

        List<Class<?>> types2 = new ArrayList<>();
        types2.add(Long.class);
        types2.add(Boolean.class);
        types2.add(Byte.class);
        Table table2 = provider.createTable("super2", types2);

        Storeable storeable = provider.createFor(table1);

        storeable.setColumnAt(0, (long) 123123123);
        storeable.setColumnAt(1, true);

        Assert.assertNull(table1.put("simple", storeable));
        storeableEquals(table1.get("simple"), storeable);

        storeable = provider.createFor(table2);

        storeable.setColumnAt(0, 1);
        storeable.setColumnAt(1, false);
        storeable.setColumnAt(2, 1);

        Assert.assertNull(table2.put("simple", storeable));
        storeableEquals(table2.get("simple"), storeable);

        table1.put("sdasd", storeable);
    }

    
    @Test
    public void testEmptyStringInStoreable() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(0, "");
        Assert.assertNull(table.put("abacaba", storeable));
        Assert.assertEquals(storeable.getStringAt(0), "");
    }


}