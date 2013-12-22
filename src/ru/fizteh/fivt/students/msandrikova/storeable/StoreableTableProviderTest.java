package ru.fizteh.fivt.students.msandrikova.storeable;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class StoreableTableProviderTest {
    private StoreableTableProvider tableProvider;
    private File path;
    private static List<Class<?>> columnTypes;

    @BeforeClass
    public static void onlyOnce() {
        columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
    }
    
    
    @After
    public void clear() throws Exception {
        if (path.exists()) {
            try {
                Utils.remover(path, "test", false);
            } catch (Exception e) {
                System.err.println("Can not remove something");
            }
        }
    }
    
    @Before
    public void setUp() throws Exception {
        path = new File(System.getProperty("user.home"), "sandbox");
        clear();
        path.mkdirs();
        tableProvider = new StoreableTableProvider(path);
    }

    @Test
    public void testRemoveTable() throws IllegalArgumentException, IOException {
        tableProvider.createTable("tableName", columnTypes);
        tableProvider.removeTable("tableName");
        assertNull(tableProvider.getTable("tableName"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistTable() throws IllegalArgumentException, IOException {
        tableProvider.removeTable("NotExist");
    }

    @Test(expected = ParseException.class)
    public void testDeserialize() throws ParseException, IOException {
        Table table = null;
        table = tableProvider.createTable("tableName", columnTypes);
        tableProvider.deserialize(table, "[\"exception\", 1]");
    }

    @Test
    public void testSerialize() throws IOException, ParseException {
        Table table = null;
        Storeable value = null;
        table = tableProvider.createTable("tableName", columnTypes);
        value = tableProvider.deserialize(table, "[1, true, null]");
        assertEquals(tableProvider.serialize(table, value), "[1,true,null]");
    }
    
    @Test(expected = ColumnFormatException.class)
    public void testSerializeException() throws ColumnFormatException, IOException, ParseException {
        Table table = null;
        Table table2 = null;
        List<Class<?>> columnTypes2 = new ArrayList<Class<?>>();
        columnTypes2.add(Float.class);
        Storeable value = null;
        table = tableProvider.createTable("tableName", columnTypes);
        table2 = tableProvider.createTable("tableName2", columnTypes2);
        value = tableProvider.deserialize(table, "[1, true, null]");
        tableProvider.serialize(table2, value);
    }


    @Test
    public void testGetTable() throws IOException {
        assertNull(tableProvider.getTable("tableName"));
        Table table = null;
        table = tableProvider.createTable("tableName", columnTypes);
        assertNotNull(tableProvider.getTable("tableName"));
        assertSame(table, tableProvider.getTable("tableName"));
    }

    @Test
    public void testCreateTable() throws IOException {
        assertNotNull(tableProvider.createTable("tableName", columnTypes));
        assertNull(tableProvider.createTable("tableName", columnTypes));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testClose() throws IllegalStateException, IllegalArgumentException, IOException {
        this.tableProvider.close();
        this.tableProvider.createTable("tableName", columnTypes);
    }

}
