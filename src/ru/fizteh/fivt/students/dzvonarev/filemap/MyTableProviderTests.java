package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyTableProviderTests {

    private TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getCanonicalPath());
    }

    @Test
    public void testSameInstanceGetCreate() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getCanonicalPath());
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        Assert.assertEquals(provider.createTable("instance", cl), provider.getTable("instance"));
        Assert.assertEquals(provider.getTable("instance"), provider.getTable("instance"));
        provider.removeTable("instance");
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingNullTable() throws IOException {
        provider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test
    public void getNonExistingTable() {
        Assert.assertNull("null", provider.getTable("testTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTable() throws IOException {
        provider.removeTable(null);
    }

    @Test
    public void getRemovedTable() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        provider.createTable("table", cl);
        provider.removeTable("table");
        Assert.assertNull("null", provider.getTable("table"));
    }

}
