package ru.fizteh.fivt.students.surakshina.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Table;

public class TableProviderTest {
    private NewTableProviderFactory factory;
    private NewTableProvider provider;
    private ArrayList<Class<?>> list;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void begin() throws IOException {
        factory = new NewTableProviderFactory();
        provider = (NewTableProvider) factory.create(folder.newFolder().toString());
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
    public void checkTableName() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkColumnName() throws IOException {
        ArrayList<Class<?>> listNew = new ArrayList<Class<?>>();
        listNew.add(Vector.class);
        provider.createTable("table1", listNew);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkColumnNameNull() throws IOException {
        provider.createTable("table", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkColumnNameEmpty() throws IOException {
        ArrayList<Class<?>> listNew = new ArrayList<Class<?>>();
        provider.createTable("table", listNew);
    }

    @Test
    public void getTableTest() throws IOException {
        Table table = provider.createTable("table", list);
        assertSame(provider.getTable("table"), table);
    }

    @Test(expected = IllegalStateException.class)
    public void checkTableExist() {
        provider.removeTable("param");
    }

    @Test(expected = IllegalStateException.class)
    public void closeRemoveTest() throws Exception {
        NewTable table = (NewTable) provider.createTable("testCloseTable", list);
        provider.close();
        provider.removeTable(table.getName());
    }

    @Test(expected = IllegalStateException.class)
    public void closeGetTest() throws Exception {
        NewTable table = (NewTable) provider.createTable("testCloseTable", list);
        provider.close();
        provider.getTable(table.getName());
    }

}
