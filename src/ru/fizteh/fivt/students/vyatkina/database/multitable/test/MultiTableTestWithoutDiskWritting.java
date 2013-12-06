package ru.fizteh.fivt.students.vyatkina.database.multitable.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.vyatkina.database.multitable.MultiTable;
import ru.fizteh.fivt.students.vyatkina.database.multitable.MultiTableProvider;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableChecker;

public class MultiTableTestWithoutDiskWritting {


    private final String SAMPLE_KEY1 = "house";
    private final String SAMPLE_VALUE1 = "ThisIsTheHouseThatJackBuilt";
    private final String NOT_EXISTING_KEY = "VeryLonelyKey";


    private final String name = "MyLittleTestTable";
    MultiTable table;
    private static MultiTableProvider tableProvider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public void setTableProvider() {
        tableProvider = new MultiTableProvider(folder.getRoot().toPath());
    }

    @Before
    public void setTable() {
        table = new MultiTable(name, tableProvider);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void putNullKeyShouldFail() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(TableChecker.KEY_SHOULD_NOT_BE_NULL);
        table.put(null, SAMPLE_VALUE1);

    }

    @Test
    public void putEmptyKeyShouldFail() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(TableChecker.KEY_SHOULD_NOT_BE_EMPTY);
        table.put("   ", SAMPLE_VALUE1);

    }


    @Test
    public void putNullValueShouldFail() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(TableChecker.VALUE_SHOULD_NOT_BE_NULL);
        table.put(SAMPLE_KEY1, null);

    }

    @Test
    public void getEmptyKeyShouldFail() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(TableChecker.KEY_SHOULD_NOT_BE_EMPTY);
        table.get("   ");

    }

    @Test
    public void getNullKeyShouldFail() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(TableChecker.KEY_SHOULD_NOT_BE_NULL);
        table.get(null);

    }

    @Test
    public void putAndGetValueShouldNotFail() {
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        Assert.assertEquals("Get value should not fail", SAMPLE_VALUE1, table.get(SAMPLE_KEY1));
    }

    @Test
    public void emptyTableShouldHaveNullSize() {
        Assert.assertEquals("Empty table should have null size", 0, table.size());
    }

    @Test
    public void putValueShouldIncreaseSize() {
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        Assert.assertEquals("Put value should increase size", 1, table.size());
    }

    @Test
    public void overrideValueShouldNotFail() {
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        Assert.assertEquals("Override value should return null", null, table.put(SAMPLE_KEY1, SAMPLE_VALUE1));
    }

    @Test
    public void overrideValueShouldNotIncreaseSize() {
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        Assert.assertEquals("Put value should increase size", 1, table.size());
    }

    @Test
    public void removeValueShouldDecreaseSize() {
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        table.remove(SAMPLE_KEY1);
        Assert.assertEquals("Remove value should decrease size", 0, table.size());
    }

    @Test
    public void removeKeyThatDoesNotExistShouldReturnNull() {
        Assert.assertEquals("Remove table that does not exist should return null", null, table.remove(NOT_EXISTING_KEY));
    }

    @Test
    public void rollbackPutShouldBeOne() {
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        Assert.assertEquals("Rollback one put should be one", 1, table.rollback());
    }

    @Test
    public void rollbackPutRemoveShouldBeNull() {
        table.put(SAMPLE_KEY1, SAMPLE_VALUE1);
        table.remove(SAMPLE_KEY1);
        Assert.assertEquals("Rollback put,remove should be 0", 0, table.rollback());
    }


}
