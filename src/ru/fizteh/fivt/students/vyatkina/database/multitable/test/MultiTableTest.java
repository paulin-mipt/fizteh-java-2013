package ru.fizteh.fivt.students.vyatkina.database.multitable.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.database.multitable.MultiTableProvider;

import java.io.IOException;

public class MultiTableTest {

    private final static String SAMPLE_TABLE_NAME1 = "MultiTableTestClassTable1";
    private final static String SAMPLE_TABLE_NAME2 = "MultiTableTestClassTable2";
    private final static String SAMPLE_KEY1 = "house";
    private final static String SAMPLE_VALUE1 = "ThisIsTheHouseThatJackBuilt";
    private Table table;

    private static MultiTableProvider tableProvider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public void setTableProvider() {
        tableProvider = new MultiTableProvider(folder.getRoot().toPath());
    }

    @Before
    public void setUseTableSampleTable1() {
        table = tableProvider.createTable(SAMPLE_TABLE_NAME1);
        if (table == null) {
            table = tableProvider.getTable(SAMPLE_TABLE_NAME1);
        }
        Assert.assertNotEquals("Current table should not be null", null, table);

    }

    @After
    public void unsetUseTable() throws IOException {
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void tableNameShouldBeSampleTableName1() {
        Assert.assertEquals("Table name should be " + SAMPLE_TABLE_NAME1, SAMPLE_TABLE_NAME1, table.getName());
    }


}
