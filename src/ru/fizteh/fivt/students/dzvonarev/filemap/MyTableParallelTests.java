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

import static org.junit.Assert.assertEquals;


public class MyTableParallelTests {

    private Table table;
    private MyStoreable store1;
    private MyStoreable store2;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        TableProvider provider = factory.create(folder.newFolder().getCanonicalPath());
        List<Class<?>> types = new ArrayList<>();
        types.add(String.class);
        table = provider.createTable("createdTable", types);
        store1 = new MyStoreable(table);
        store2 = new MyStoreable(table);
        store1.setColumnAt(0, "example");
        store2.setColumnAt(0, "example2");
    }

    @Test
    public void testMakeChanges() {
        table.put("blabla", store1);
        Thread newThread = new Thread() {
            public void run() {
                table.put("blabla", store2);
                table.put("qwerty", store2);
            }
        };
        try {
            newThread.start();
            newThread.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals("error: not equal", store1, table.get("blabla"));
        Assert.assertNull("error", table.get("qwerty"));
    }

    @Test
    public void testCommitRollback() {
        Thread newThread = new Thread() {
            public void run() {
                table.put("key", store1);
                try {
                    table.commit();
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
        Assert.assertEquals("can't get commited value", store1, table.get("key"));
        Assert.assertEquals("wrong size", table.size(), 1);
        newThread = new Thread() {
            public void run() {
                table.put("blabla", store1);
                table.put("key", store2);
                assertEquals(table.rollback(), 2);
            }
        };
        try {
            newThread.start();
            newThread.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        assertEquals(table.rollback(), 0);
    }


}
