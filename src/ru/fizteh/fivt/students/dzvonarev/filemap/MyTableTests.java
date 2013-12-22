package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MyTableTests {

    private Table table;
    private TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getCanonicalPath());
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        table = provider.createTable("testTable", cl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValue() {
        table.put("key", null);
    }

    @Test
    public void testIsCorrect() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        Table t = provider.createTable("correct", cl);
        List<Object> data = new ArrayList<>();
        data.add(2);
        data.add("qwerty");
        data.add(3.3);
        Storeable st = provider.createFor(t, data);
        table.put("blabla", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void testLessArguments() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        Table newTable = provider.createTable("less", cl);
        List<Object> args = new ArrayList<>();
        args.add(24);
        args.add("less");
        Storeable st = provider.createFor(newTable, args);
        table.put("less", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void testMoreArguments() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        cl.add(Integer.class);
        Table newTable = provider.createTable("more", cl);
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add("2");
        args.add(3.4);
        args.add(1);
        Storeable st = provider.createFor(newTable, args);
        table.put("more", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongTypes() {
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        Storeable st = provider.createFor(table, args);
        table.put("wrongTypes", st);
    }

    @Test
    public void ifHaveNulls() {
        List<Object> args = new ArrayList<>();
        args.add(null);
        args.add("null");
        args.add(null);
        Storeable st = provider.createFor(table, args);
        table.put("hasnull", st);
    }

    @Test
    public void testCommit() throws IOException {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(1, "string");
        st.setColumnAt(2, 2.5);
        table.put("key1", st);
        st.setColumnAt(0, 2);
        st.setColumnAt(1, "string2");
        st.setColumnAt(2, 4.5);
        table.put("key2", st);
        assertEquals(table.commit(), 2);
    }

    @Test
    public void testRollback() throws IOException {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 3);
        st.setColumnAt(1, "stringHere");
        st.setColumnAt(2, 2.666);
        table.put("keyNew", st);
        assertEquals(table.commit(), 1);
        assertEquals(table.commit(), 0);
        assertNotNull(table.remove("keyNew"));
        assertEquals(table.rollback(), 1);
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseGetPutRemove() {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(1, "string");
        st.setColumnAt(2, 2.5);
        table.put("key1", st);
        MyTable oldTable = (MyTable) table;
        oldTable.close();
        table.get("key1");
    }

    @Test(expected = IllegalStateException.class)
    public void testClosePut() {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(1, "string");
        st.setColumnAt(2, 2.5);
        table.put("key1", st);
        MyTable oldTable = (MyTable) table;
        oldTable.close();
        table.put("newKey", st);
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseRemove() throws IOException {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(1, "string");
        st.setColumnAt(2, 2.5);
        table.put("key1", st);
        st.setColumnAt(0, 2);
        st.setColumnAt(1, "string2");
        st.setColumnAt(2, 4.5);
        table.put("key2", st);
        MyTable oldTable = (MyTable) table;
        oldTable.close();
        table.remove("key2");
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseCommit() throws IOException {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(1, "string");
        st.setColumnAt(2, 2.5);
        table.put("key1", st);
        st.setColumnAt(0, 2);
        st.setColumnAt(1, "string2");
        st.setColumnAt(2, 4.5);
        table.put("key2", st);
        MyTable oldTable = (MyTable) table;
        oldTable.close();
        table.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseRollback() throws IOException {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(1, "string");
        st.setColumnAt(2, 2.5);
        table.put("key1", st);
        st.setColumnAt(0, 2);
        st.setColumnAt(1, "string2");
        st.setColumnAt(2, 4.5);
        table.put("key2", st);
        MyTable oldTable = (MyTable) table;
        oldTable.close();
        table.rollback();
    }

    @Test
    public void testGetClosedTable() throws IOException {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(1, "string");
        st.setColumnAt(2, 2.5);
        table.put("succes", st);
        table.commit();
        MyTable oldTable = (MyTable) table;
        oldTable.close();
        Table newTable = provider.getTable("testTable");
        assertNotEquals(oldTable, newTable);
        assertNotEquals(newTable.get("succes"), null);
    }

}
