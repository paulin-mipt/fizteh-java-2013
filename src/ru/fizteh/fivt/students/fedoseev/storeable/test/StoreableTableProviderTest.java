package ru.fizteh.fivt.students.fedoseev.storeable.test;

import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.fedoseev.storeable.ColumnTypes;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.fedoseev.storeable.StoreableTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreableTableProviderTest {
    private static StoreableTableProvider tp;
    private static File dbDir;
    private static List<Class<?>> types;

    public StoreableTableProviderTest() throws IOException {
        dbDir = new File("database");
        types = new ArrayList<>();

        dbDir.mkdirs();

        tp = new StoreableTableProviderFactory().create("database");
        types = ColumnTypes.getTypesList();
    }

    @After
    public void tearDown() {
        dbDir.delete();
    }

    @Test
    public void testCreateTable() throws Exception {
        tp.createTable("chakachaka", types);
    }

    @Test
    public void testCreateExistingTable() throws Exception {
        tp.createTable("bombom", types);
        tp.createTable("bombom", types);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullNameTable() throws Exception {
        tp.createTable(null, types);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTypesTable() throws Exception {
        tp.createTable("chuchu", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateIllegalTypesTable() throws Exception {
        List<Class<?>> types = new ArrayList<>();

        types.add(Arrays.class);
        tp.createTable("obama", types);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyTypeListTable() throws Exception {
        List<Class<?>> types = new ArrayList<>();

        tp.createTable("barokko", types);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyNameTable() throws Exception {
        tp.createTable("", types);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateIllegalSymbolTable() throws Exception {
        tp.createTable(".oops.", types);
    }
}
