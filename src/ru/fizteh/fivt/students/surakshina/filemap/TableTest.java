package ru.fizteh.fivt.students.surakshina.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableTest {
    private TableProviderFactory factory;
    private TableProvider provider;
    private ArrayList<Class<?>> list;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void begin() throws IOException {
        factory = new NewTableProviderFactory();
        provider = factory.create(folder.newFolder().toString());
        list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        list.add(Double.class);
        list.add(Float.class);
        list.add(String.class);
        list.add(Boolean.class);
        list.add(Byte.class);
        list.add(Long.class);

    }

    @Test(expected = IllegalArgumentException.class)
    public void checkKeyWithWhiteSpace() {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 5);
        table.put("  sad ", st);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        table.get(null);
    }

    @Test
    public void getExistsKey() throws Exception {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        assertNull(table.put("keyX", st));
        assertNotNull(table.get("keyX"));
        assertNotNull(table.remove("keyX"));
        assertNull(table.get("keyX"));
    }

    @Test
    public void testSize() throws IOException {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        Storeable st = new MyStoreable(table);
        Storeable st1 = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st1.setColumnAt(0, 7);
        table.put("key1", st);
        table.put("key2", st1);
        table.commit();
        assertEquals(table.size(), 2);

    }

    @Test
    public void testCommit() throws IOException {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        Storeable st = new MyStoreable(table);
        Storeable st1 = new MyStoreable(table);
        st.setColumnAt(0, 1000);
        st1.setColumnAt(0, 11222);
        table.put("sss", st);
        table.put("ssssss", st1);
        assertNotNull(table.remove("sss"));
        assertEquals(table.commit(), 1);

    }

    @Test
    public void testRollback() throws IOException {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        Storeable st = new MyStoreable(table);
        Storeable st1 = new MyStoreable(table);
        st.setColumnAt(0, 33);
        st1.setColumnAt(0, 100000);
        table.put("s", st);
        table.put("saws", st1);
        assertEquals(table.commit(), 2);
        assertEquals(table.rollback(), 0);
        assertEquals(table.commit(), 0);
        assertNotNull(table.remove("s"));
        assertEquals(table.rollback(), 1);

    }

    @Test
    public void testGetColumnsCount() {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        assertEquals(table.getColumnsCount(), 7);
    }

    @Test
    public void testGetColumnType() {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        assertEquals(table.getColumnType(1), Double.class);
        assertEquals(table.getColumnType(0), Integer.class);
    }

    @Test(expected = IllegalStateException.class)
    public void closePutGetClose() throws IOException {
        NewTable table = (NewTable) provider.createTable("table", list);
        Storeable st1 = new MyStoreable(table);
        st1.setColumnAt(0, 1);
        table.put("key", st1);
        table.close();
        table.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void closeCommitTest() throws Exception {
        NewTable table = (NewTable) provider.createTable("table", list);
        Storeable st1 = new MyStoreable(table);
        st1.setColumnAt(0, 1);
        table.put("key", st1);
        table.close();
        table.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void closeRollbackTest() throws Exception {
        NewTable table = (NewTable) provider.createTable("table", list);
        Storeable st1 = new MyStoreable(table);
        st1.setColumnAt(0, 1);
        table.put("key", st1);
        table.close();
        table.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void closeSizeTest() throws Exception {
        NewTable table = (NewTable) provider.createTable("table", list);
        Storeable st1 = new MyStoreable(table);
        st1.setColumnAt(0, 1);
        table.put("key", st1);
        table.close();
        table.size();
    }

    @Test(expected = IllegalStateException.class)
    public void closeRemoveTest() throws Exception {
        NewTable table = (NewTable) provider.createTable("table", list);
        Storeable st1 = new MyStoreable(table);
        st1.setColumnAt(0, 1);
        table.put("key", st1);
        table.close();
        table.remove("key");
    }
}
