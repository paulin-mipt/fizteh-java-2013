package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyTableProviderParallelTests {

    private TableProvider provider;
    private List<Class<?>> types;
    private Table table;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getCanonicalPath());
        types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Integer.class);
        types.add(Integer.class);
    }

    @Test
    public void getCreated() throws IOException {
        Thread newThread = new Thread() {
            public void run() {
                try {
                    table = provider.createTable("table", types);
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        try {
            newThread.start();
            newThread.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertSame("error: should get table", provider.getTable("table"), table);
    }

    @Test
    public void getRemoved() throws IOException {
        provider.createTable("table", types);
        Thread newThread = new Thread() {
            public void run() {
                try {
                    provider.removeTable("table");
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        try {
            newThread.start();
            newThread.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNull("error: should be no table", provider.getTable("table"));
    }

}
