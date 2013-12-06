package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.junit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.MyTableProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParallelTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    File f;
    TableProvider provider;
    List<Class<?>> list;
    Table table;

    @Before
    public void setUp() throws Exception {
        f = folder.newFolder();
        provider = new MyTableProvider(f);
        list = new ArrayList<>();
        list.add(Integer.class);
    }

    @Test
    public void createGetTableTest() throws Exception {
        class SecondThread extends Thread {
            @Override
            public void run() {
                try {
                    table = provider.createTable("table", list);
                    Assert.assertNotNull(table);
                    Assert.assertNull(provider.createTable("table", list));
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        Thread first = new SecondThread();

        first.start();
        first.join();

        Assert.assertNull(provider.createTable("table", list));
        Assert.assertSame(provider.getTable("table"), table);
    }

    @Test
    public void putCommitTest() throws Exception {
        table = provider.createTable("table", list);
        final Storeable st = provider.createFor(table);

        Thread first = new Thread() {
            @Override
            public void run() {
                Assert.assertNull(table.put("key", st));
                try {
                    Assert.assertEquals(table.commit(), 1);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };

        first.start();
        first.join();

        Assert.assertNotNull(table.put("key", st));
    }

    @Test
    public void separatePutTest() throws Exception {
        File f = folder.newFolder("test");
        final TableProvider provider = new MyTableProvider(f);
        final List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        final Table table = provider.createTable("table", list);
        final Storeable st = provider.createFor(table);

        Thread first = new Thread() {
            @Override
            public void run() {
                Assert.assertNull(table.put("key", st));
            }
        };

        first.start();
        first.join();

        Assert.assertNull(table.put("key", st));
    }

    @Test
    public void sizeTest() throws Exception {
        File f = folder.newFolder("test");
        final TableProvider provider = new MyTableProvider(f);
        final List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);
        final Table table = provider.createTable("table", list);
        final Storeable st = provider.createFor(table);

        Thread first = new Thread() {
            @Override
            public void run() {
                table.put("key2", st);
                try {
                    table.commit();
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };

        List<Object> values = new ArrayList<>();
        values.add(1);
        Assert.assertNull(table.put("key", provider.createFor(table, values)));
        Assert.assertEquals(table.size(), 1);
        first.start();
        first.join();
        Assert.assertEquals(table.size(), 2);
        table.put("key2", provider.createFor(table, values));
        Assert.assertEquals(table.size(), 2);

    }
}
