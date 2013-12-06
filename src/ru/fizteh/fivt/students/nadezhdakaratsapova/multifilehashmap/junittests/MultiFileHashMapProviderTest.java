package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.junittests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;

import java.io.File;

public class MultiFileHashMapProviderTest {
    private static final String TESTED_DIRECTORY = "JavaTests";
    private TableProvider tableProvider;


    @Before
    public void setUp() throws Exception {
        File dir = new File(TESTED_DIRECTORY);
        dir = dir.getCanonicalFile();
        dir.mkdir();
        tableProvider = new MultiFileHashMapProvider(dir);
    }

    @After
    public void tearDown() throws Exception {
        File dir = new File(TESTED_DIRECTORY);
        dir = dir.getCanonicalFile();
        CommandUtils.recDeletion(dir);

    }

    @Test
    public void createValidTableTest() {
        Assert.assertNotNull(tableProvider.createTable("newTable"));
        Assert.assertNull(tableProvider.createTable("newTable"));
        tableProvider.removeTable("newTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullNameTableShouldFail() {
        tableProvider.createTable(null);
    }

    @Test(expected = RuntimeException.class)
    public void createNotCorrectNameTableShouldFail() {
        tableProvider.createTable("    ]]]*/   ");
    }


    @Test
    public void getValidTableTest() {
        Assert.assertNull(tableProvider.getTable("MyFavouriteTable"));
        Table newTable = tableProvider.createTable("MyFavouriteTable");
        Assert.assertSame(tableProvider.getTable("MyFavouriteTable"), newTable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullNameTableShouldFail() {
        tableProvider.getTable(null);
    }

    @Test(expected = RuntimeException.class)
    public void getNotCorrectNameTableShouldFail() {
        tableProvider.getTable("[][][]***");
    }

    @Test
    public void removeValidTableTest() {
        tableProvider.createTable("SuperTable");
        tableProvider.removeTable("SuperTable");
        Assert.assertNull(tableProvider.getTable("SuperTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullNameTable() {
        tableProvider.removeTable(null);
    }

    @Test(expected = RuntimeException.class)
    public void removeNotCorrectNameTable() {
        tableProvider.removeTable("qwerty_56");
    }

    @Test(expected = IllegalStateException.class)
    public void removeNotExistingTable() {
        tableProvider.removeTable("TestTable");
    }

}
