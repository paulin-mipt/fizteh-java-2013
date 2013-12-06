package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * User: Alexander
 * Date: 14.11.13
 * Time: 0:02
 */
public class ParallelUnitTest {
    TableProvider tableProvider;
    Table testTable;
    Storeable testStoreable;
    List<Class<?>> typesTestListOne;
    File sandBoxDirectory = new File("/Users/Alexander/Documents/JavaDataBase/Tests");

    @Before
    public void setUpTestObject() throws IOException, ParseException {
        tableProvider = new StoreableTableProvider(sandBoxDirectory);
        typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        testTable = tableProvider.createTable("testParallelTable", typesTestListOne);
        testStoreable = tableProvider.deserialize(testTable, "[1,2,3]");
    }

    @After
    public void tearDownTestObject() throws IOException {
        tableProvider.removeTable("testParallelTable");
        DeleteDirectory.rm(sandBoxDirectory);
    }

    @Test
    public void threadSimpleWorkTest() throws Exception {
        Thread firstTestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                testTable.put("firstKey", testStoreable);
                try {
                    testTable.commit();
                } catch (IOException e) {
                    throw new IllegalArgumentException("thread simple test: commit error");
                }
            }
        });

        Thread secondTestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                testTable.put("secondKey", testStoreable);
                try {
                    testTable.commit();
                } catch (IOException e) {
                    throw new IllegalArgumentException("thread simple test: commit error");
                }
            }
        });

        firstTestThread.run();
        secondTestThread.run();

        firstTestThread.interrupt();
        secondTestThread.interrupt();

        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTable, testTable.get("firstKey")));
        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTable, testTable.get("secondKey")));
    }

    @Test
    public void threadCreateTablesTest() throws Exception {
        Thread firstTestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tableProvider.createTable("parallelTable", typesTestListOne);
                    tableProvider.getTable("parallelNotExTable");
                } catch (IOException e) {
                    throw new IllegalArgumentException("thread create table test: error");
                }
            }
        });

        Thread secondTestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tableProvider.createTable("parallelNotExTable", typesTestListOne);
                    tableProvider.createTable("parallelTable", typesTestListOne);
                    tableProvider.getTable("parallelTable");
                } catch (IOException e) {
                    throw new IllegalArgumentException("thread create table test: error");
                }
            }
        });

        firstTestThread.run();
        secondTestThread.run();

        firstTestThread.interrupt();
        secondTestThread.interrupt();
    }
}
