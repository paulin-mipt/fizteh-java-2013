package ru.fizteh.fivt.students.ermolenko.storable.tests;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapUtils;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTableProvider;
import ru.fizteh.fivt.students.ermolenko.storable.StoreableTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableTableProviderTest {

    private static File database;
    private static StoreableTableProvider tableProvider;
    private static ArrayList<Class<?>> typeList = new ArrayList<Class<?>>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        database = new File("javatest").getCanonicalFile();
        database.mkdir();
        StoreableTableProviderFactory factory = new StoreableTableProviderFactory();
        tableProvider = factory.create(database.toString());
        typeList.add(Integer.class);
        typeList.add(String.class);
        typeList.add(Boolean.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        File file = new File("javatest");
        if (file.exists()) {
            MultiFileHashMapUtils.deleteDirectory(file);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNulFile() throws Exception {

        tableProvider.createTable(null, typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDontMachesNameTable() throws Exception {

        tableProvider.createTable("#2@@", typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyNameTable() throws Exception {

        tableProvider.createTable("", typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyTypeList() throws Exception {

        tableProvider.createTable("normalName", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadTypeList() throws Exception {

        ArrayList<Class<?>> wrongTypes = new ArrayList<Class<?>>();
        wrongTypes.add(List.class);
        tableProvider.createTable("anotherNormalName", wrongTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteTableWithNullName() throws Exception {

        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteNotExistingTable() throws Exception {

        tableProvider.removeTable("notExistingTable");
    }

    @Test
    public void createExistingTable() throws Exception {

        tableProvider.createTable("newTable", typeList);
        Assert.assertNull(tableProvider.createTable("newTable", typeList));
        tableProvider.removeTable("newTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyNameOfTable() {

        tableProvider.getTable("");
    }

    @Test
    public void testThreadsCreate() throws Exception {

        Thread firstThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    tableProvider.createTable("myFirstFavouriteThreadTable", typeList);
                    tableProvider.getTable("mySecondThreadTable");
                } catch (IOException e) {
                    throw new IllegalArgumentException("something going wrong");
                }
            }
        });

        Thread secondThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    tableProvider.createTable("mySecondThreadTable", typeList);
                    tableProvider.createTable("myFirstFavouriteThreadTable", typeList);
                    tableProvider.getTable("myFirstFavouriteThreadTable");
                } catch (IOException e) {
                    throw new IllegalArgumentException("something going wrong");
                }
            }
        });

        firstThread.run();
        secondThread.run();

        firstThread.interrupt();
        secondThread.interrupt();
    }
}
