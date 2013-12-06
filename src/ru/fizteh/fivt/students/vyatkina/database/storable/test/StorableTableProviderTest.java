package ru.fizteh.fivt.students.vyatkina.database.storable.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.database.storable.StorableTableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderConstants;
import ru.fizteh.fivt.students.vyatkina.database.superior.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StorableTableProviderTest implements TableProviderConstants {

    private TableProviderFactory factory = new StorableTableProviderFactory();
    private TableProvider tableProvider;
    private final String NORMAL_TABLE_NAME = "OkTable";
    private final String RUSSIAN_NORMAL_TABLE = "ХорошийТаблица";
    private final String FUNNY_TABLE_NAME = "^_^";
    private final List<Class<?>> classList;

    public StorableTableProviderTest() {
        classList = new ArrayList<>();
        for (Class<?> clazz : Type.BY_CLASS.keySet()) {
            classList.add(clazz);
        }
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void initializeTableProvider() throws IOException {
        tableProvider = factory.create(folder.getRoot().toString());
    }

    @Test
    public void getTableShouldReturnNullIfTableNotExists() {
        assertEquals("Get not existing table should be null", null, tableProvider.getTable(NORMAL_TABLE_NAME));
    }

    @Test
    public void createTableAndThenGetItShouldReturnTheSame() throws IOException {
        Table table = tableProvider.createTable(NORMAL_TABLE_NAME, classList);
        assertEquals("Create table and get table should return the same",
                table, tableProvider.getTable(NORMAL_TABLE_NAME));
        assertEquals("Create table and get table should return the same",
                table, tableProvider.getTable(NORMAL_TABLE_NAME));
    }

    @Test
    public void createAndGetTableWithRussianNameShouldNotFail() throws IOException {
        Table table = tableProvider.createTable(RUSSIAN_NORMAL_TABLE, classList);
        assertEquals("Create table and get russian table should return the same",
                table, tableProvider.getTable(RUSSIAN_NORMAL_TABLE));
    }

    @Test
    public void createTableWithStrangeNameShouldFail() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(TableProviderChecker.UNSUPPORTED_TABLE_NAME);
        tableProvider.createTable(FUNNY_TABLE_NAME, classList);
    }

    @Test
    public void removeTableShouldNotExist() throws IOException {
        tableProvider.createTable(RUSSIAN_NORMAL_TABLE, classList);
        tableProvider.removeTable(RUSSIAN_NORMAL_TABLE);
        assertEquals("Table should be removed", null, tableProvider.getTable(RUSSIAN_NORMAL_TABLE));
    }

}
