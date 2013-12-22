package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.junittests;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableDataValue;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableTableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableTest {
    private static final String TESTED_DIRECTORY = "JavaTests";
    private static final String TESTED_TABLE = "MyFavouriteTable";
    private StoreableTable dataTable;
    private File testedFile = new File(TESTED_DIRECTORY);
    private List<Class<?>> types;
    private StoreableTableProvider tableProvider;
    private static boolean firstThreadFlag = true;
    private static boolean secondThreadFlag = true;

    @Before
    public void setUp() throws Exception {
        types = new ArrayList<Class<?>>();
        types.add(Integer.class);
        types.add(String.class);
        if (!testedFile.exists()) {
            testedFile.mkdir();
        }
        tableProvider = new StoreableTableProvider(testedFile);
        dataTable = tableProvider.createTable(TESTED_TABLE, types);
        tableProvider.setCurTable(TESTED_TABLE);

    }

    @After
    public void tearDown() throws Exception {
        CommandUtils.recDeletion(testedFile);
    }

    @Test
    public void getNameTest() throws Exception {
        Assert.assertEquals(dataTable.getName(), TESTED_TABLE);
    }

    @Test
    public void getValidKey() throws Exception {
        Assert.assertNull(dataTable.get("one"));
        Storeable value = new StoreableDataValue(types);
        value.setColumnAt(0, 5);
        value.setColumnAt(1, "book");
        dataTable.put("favourite", value);
        Assert.assertEquals(dataTable.get("favourite"), value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKeyShouldFail() throws Exception {
        dataTable.get(null);
    }

    @Test
    public void putValidValueTest() {
        Storeable firstValue = new StoreableDataValue(types);
        firstValue.setColumnAt(0, 56);
        firstValue.setColumnAt(1, "pages");
        Storeable secondValue = new StoreableDataValue(types);
        secondValue.setColumnAt(0, 40);
        secondValue.setColumnAt(1, "pages");
        dataTable.put("qwerty", secondValue);
        Assert.assertEquals(dataTable.put("qwerty", firstValue), secondValue);
        Assert.assertNull(dataTable.put("key", firstValue));
    }


    @Test(expected = IllegalArgumentException.class)
    public void putNullValueShouldFail() throws Exception {
        dataTable.put("moo", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKeyShouldFail() throws IOException {
        dataTable.put(null, new StoreableDataValue(types));
    }

    @Test
    public void removeValidKeyTest() {
        Assert.assertNull(dataTable.remove("chair"));
        Storeable firstValue = new StoreableDataValue(types);
        firstValue.setColumnAt(0, 2);
        firstValue.setColumnAt(1, "black");
        dataTable.put("cats", firstValue);
        Assert.assertEquals(dataTable.remove("cats"), firstValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKeyShouldFail() {
        dataTable.remove(null);
    }

    @Test
    public void sizeTest() throws Exception {
        Assert.assertEquals(dataTable.size(), 0);
        dataTable.put("moo", tableProvider.deserialize(dataTable, "<row><col>5</col><col>text</col></row>"));
        dataTable.remove("moo");
        dataTable.remove("foo");
        dataTable.put("newValue", tableProvider.deserialize(dataTable, "<row><col>78</col><col>car</col></row>"));
        Assert.assertEquals(dataTable.size(), 1);
    }

    @Test
    public void commitTest() throws Exception {
        dataTable.put("1", tableProvider.deserialize(dataTable, "<row><col>98</col><col>bear</col></row>"));
        dataTable.put("3", tableProvider.deserialize(dataTable, "<row><col>5</col><col>pig</col></row>"));
        dataTable.remove("3");
        Assert.assertEquals(dataTable.commit(), 1);
    }

    @Test
    public void rollbackTest() throws Exception {
        dataTable.put("7", tableProvider.deserialize(dataTable, "<row><col>5</col><col>text</col></row>"));
        dataTable.remove("7");
        Assert.assertEquals(dataTable.rollback(), 0);
        dataTable.put("8", tableProvider.deserialize(dataTable, "<row><col>45</col><col>text</col></row>"));
        dataTable.remove("7");
        Assert.assertEquals(dataTable.rollback(), 1);

    }

    @Test
    public void getColumnsCountTest() throws Exception {
        Assert.assertEquals(dataTable.getColumnsCount(), types.size());
    }

    @Test
    public void getValidColumnType() throws Exception {
        Assert.assertEquals(dataTable.getColumnType(0), Integer.class);
        Assert.assertEquals(dataTable.getColumnType(1), String.class);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getNotExistingColumnTypeShouldFail() {
        dataTable.getColumnType(35);
    }

    @Test
    public void putSameValueFromDifferentThreadsCommit() throws Exception {
        firstThreadFlag = true;
        secondThreadFlag = true;
        Thread firstThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataTable.put("key", tableProvider.deserialize(dataTable,
                            "<row><col>5</col><col>text</col></row>"));
                    firstThreadFlag = (dataTable.commit() == 1);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e.getMessage());
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        });
        Thread secondThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataTable.put("key", tableProvider.deserialize(dataTable,
                            "<row><col>5</col><col>text</col></row>"));
                    secondThreadFlag = (dataTable.commit() == 1);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e.getMessage());
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        });
        firstThread.start();
        secondThread.start();
        firstThread.join();
        secondThread.join();
        Assert.assertTrue(firstThreadFlag ^ secondThreadFlag);
    }

    @Test(expected = IllegalStateException.class)
    public void closeTableGetShouldFail() throws Exception {
        dataTable.close();
        dataTable.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void closeTablePutValueShouldFail() throws Exception {
        dataTable.close();
        Storeable value = new StoreableDataValue(types);
        value.setColumnAt(0, 5);
        value.setColumnAt(1, "book");
        dataTable.put("key", value);
    }
}
