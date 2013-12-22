package ru.fizteh.fivt.students.inaumov.proxy.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTable;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProxyTest {
    private static final String DATABASE_PATH = "./proxy_test";
    TableProvider tableProvider;
    Table currentTable;

    @Before
    public void setup() {
        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            tableProvider = factory.create(DATABASE_PATH);
        } catch (IOException e) {
            //
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>() { {
            add(Integer.class);
            add(String.class);
        }};

        try {
            currentTable = tableProvider.createTable("test", columnTypes);
        } catch (IOException e) {
            //
        }
    }

    @After
    public void after() {
        try {
            tableProvider.removeTable("test");
        } catch (IOException e) {
            //
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseGet() throws Exception {
        DatabaseTable table = (DatabaseTable) currentTable;
        table.close();
        table.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testClosePut() throws Exception {
        DatabaseTable table = (DatabaseTable) currentTable;
        table.close();
        table.put("key", tableProvider.deserialize(currentTable, "[2, 'hello']"));
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseRollback() throws Exception {
        DatabaseTable table = (DatabaseTable) currentTable;
        table.close();
        table.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseGetColumnsCount() throws Exception {
        DatabaseTable table = (DatabaseTable) currentTable;
        table.close();
        table.getColumnsCount();
    }

    @Test
    public void testStoreableToString() throws Exception {
        List<Object> values = new ArrayList<Object>() { {
            add(1);
            add("hello");
        }};

        Storeable storeable = tableProvider.createFor(currentTable, values);
        Assert.assertEquals("DatabaseRow[1,hello]", storeable.toString());

        values.set(0, null);
        storeable = tableProvider.createFor(currentTable, values);
        Assert.assertEquals("DatabaseRow[,hello]", storeable.toString());
    }

    @Test
    public void testTableToString() throws Exception {
        File dbDirFile = new File(System.getProperty("user.dir"), DATABASE_PATH);
        File tableDirFile = new File(dbDirFile, "test");

        Assert.assertEquals("DatabaseTable[" + tableDirFile.getAbsolutePath() + "]", currentTable.toString());

    }

    @Test
    public void testTableProviderToString() throws Exception {
        File dbDirFile = new File(System.getProperty("user.dir"), DATABASE_PATH);
        Assert.assertEquals("DatabaseTableProvider[" + dbDirFile.getAbsolutePath() + "]", tableProvider.toString());
    }
}
