package ru.fizteh.fivt.students.inaumov.storeable.tests;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableTest {
    private TableProvider tableProvider = null;
    private Table currentTable = null;

    private static final String TEST_DATABASE_NAME = "table";
    private static final String TEST_DATABASE_DIR = "./storeable_test";
    @Before
    public void setup() {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            tableProvider = factory.create(TEST_DATABASE_DIR);
        } catch (IOException e) {
            //
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);

        try {
            currentTable = tableProvider.createTable(TEST_DATABASE_NAME, columnTypes);
        } catch (IOException e) {
            //
        }
    }

    @After
    public void after() {
        try {
            tableProvider.removeTable(TEST_DATABASE_NAME);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void putEmptyValueShouldNotFail() throws Exception {
        String key = "key";
        String value = "[\"\", \"valueNotEmpty\", 3]";
        currentTable.put(key, tableProvider.deserialize(currentTable, value));
        currentTable.commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void putEmptyKeyShouldFail() throws Exception {
        String key = "";
        String value = "[\"\", \"valueNotEmpty\", 3]";
        currentTable.put(key, tableProvider.deserialize(currentTable, value));
        currentTable.commit();
    }
}
