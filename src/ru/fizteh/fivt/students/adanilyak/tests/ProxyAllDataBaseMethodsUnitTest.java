package ru.fizteh.fivt.students.adanilyak.tests;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.adanilyak.proxy.ProxyLoggingFactoryImplementation;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProviderFactory;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.StringCreationTools;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexander
 * Date: 29.11.13
 * Time: 4:42
 */
public class ProxyAllDataBaseMethodsUnitTest {
    Object testProxy;
    ProxyLoggingFactoryImplementation testProxyLoggingFactory;
    StringWriter writer;
    String sandBoxDirectory;
    File sandBoxFile;
    List<Class<?>> typesTestList;

    StoreableTableProviderFactory testFactory;
    StoreableTableProvider testProvider;
    StoreableTable testTable;
    StoreableRow testRow;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUpTestObject() throws IOException, ParseException {
        writer = new StringWriter();
        testProxyLoggingFactory = new ProxyLoggingFactoryImplementation();

        sandBoxFile = folder.newFolder();
        sandBoxDirectory = sandBoxFile.getCanonicalPath();

        testFactory = new StoreableTableProviderFactory();
        testProvider = (StoreableTableProvider) testFactory.create(sandBoxDirectory);

        typesTestList = WorkWithStoreableDataBase.createListOfTypesFromString("int boolean String");

        testTable = (StoreableTable) testProvider.createTable("testTable", typesTestList);
        testRow = (StoreableRow) testProvider.deserialize(testTable, "[0,true,null]");

        testTable.put("one", testProvider.deserialize(testTable, "[1, false, \"first\"]"));
        testTable.put("two", testProvider.deserialize(testTable, "[2, true, \"second\"]"));
        testTable.put("three", testProvider.deserialize(testTable, "[3, false, \"third\"]"));
    }

    @After
    public void tearDownTestObject() throws IOException {
        DeleteDirectory.rm(new File(sandBoxDirectory));
    }

    /**
     * TEST BLOCK
     * STOREABLE INTERFACE TESTS
     */

    @Test
    public void setColumnAtProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        ((Storeable) testProxy).setColumnAt(0, 57);
        Assert.assertEquals("StoreableRow[57,true,]", testProxy.toString());
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow\" "
                + "name=\"setColumnAt\"><arguments><argument>0</argument><argument>57</argument></arguments></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void getColumnAtProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        ((Storeable) testProxy).getColumnAt(0);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow\" "
                + "name=\"getColumnAt\"><arguments><argument>0</argument></arguments><return>0</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void getIntAtProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        ((Storeable) testProxy).getColumnAt(0);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow\" "
                + "name=\"getColumnAt\"><arguments><argument>0</argument></arguments><return>0</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void getBooleanAtProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        ((Storeable) testProxy).getColumnAt(1);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow\" "
                + "name=\"getColumnAt\"><arguments><argument>1</argument></arguments><return>true</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void getNullStringAtProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        ((Storeable) testProxy).getColumnAt(2);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow\" "
                + "name=\"getColumnAt\"><arguments><argument>2</argument></arguments><return><null></null></return>"
                + "</invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void getOutOfBoundColumnAtProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        boolean excWasThrown = false;
        try {
            ((Storeable) testProxy).getColumnAt(3);
        } catch (IndexOutOfBoundsException exc) {
            excWasThrown = true;
        }
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow\" "
                + "name=\"getColumnAt\"><arguments><argument>3</argument></arguments>"
                + "<thrown>java.lang.IndexOutOfBoundsException: get column at: bad index</thrown></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
        Assert.assertTrue(excWasThrown);
    }

    @Test
    public void storeableToStringNotProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        testProxy.toString();
        Assert.assertEquals("", writer.toString());
    }

    @Test
    public void storeableHashCodeNotProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        testProxy.hashCode();
        Assert.assertEquals("", writer.toString());
    }

    /**
     * TEST BLOCK
     * TABLE INTERFACE TESTS
     */

    @Test
    public void getTableNameProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).getName();
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"getName\"><arguments></arguments><return>testTable</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void getProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).get("one");
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"get\"><arguments><argument>one</argument></arguments><return>StoreableRow[1,false,first]"
                + "</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void putProxyTest() throws ParseException {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).put("four", testProvider.deserialize(testTable, "[4, true, \"fourth\"]"));
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"put\"><arguments><argument>four</argument>"
                + "<argument>StoreableRow[4,true,fourth]</argument></arguments><return><null></null></return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void removeProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).remove("one");
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"remove\"><arguments><argument>one</argument></arguments><return>StoreableRow[1,false,first]"
                + "</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void sizeProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).size();
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"size\"><arguments></arguments><return>3</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void commitProxyTest() throws IOException {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).commit();
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"commit\"><arguments></arguments><return>3</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void rollbackProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).rollback();
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"rollback\"><arguments></arguments><return>3</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void getColumnsCountProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        ((Table) testProxy).getColumnsCount();
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable\" "
                + "name=\"getColumnsCount\"><arguments></arguments><return>3</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void tableToStringNotProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        testProxy.toString();
        Assert.assertEquals("", writer.toString());
    }

    @Test
    public void tableHashCodeNotProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testTable, Table.class);
        testProxy.hashCode();
        Assert.assertEquals("", writer.toString());
    }

    /**
     * TEST BLOCK
     * TABLE PROVIDER INTERFACE TESTS
     */

    @Test
    public void getTableProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        ((TableProvider) testProxy).getTable("testTable");
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider\" "
                + "name=\"getTable\"><arguments><argument>testTable</argument></arguments>"
                + "<return>StoreableTable[" + sandBoxDirectory + "/testTable]</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void createTableProxyTest() throws IOException {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        ((TableProvider) testProxy).createTable("newTestTable", typesTestList);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider\" "
                + "name=\"createTable\"><arguments><argument>newTestTable</argument><argument>"
                + "<list><value>class java.lang.Integer</value><value>class java.lang.Boolean</value>"
                + "<value>class java.lang.String</value></list></argument></arguments>"
                + "<return>StoreableTable[" + sandBoxDirectory + "/newTestTable]"
                + "</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void removeTableProxyTest() throws IOException {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        ((TableProvider) testProxy).removeTable("testTable");
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider\" "
                + "name=\"removeTable\"><arguments><argument>testTable</argument></arguments></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void deserializeProxyTest() throws ParseException {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        ((TableProvider) testProxy).deserialize(testTable, "[4, false, \"fourth\"]");
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider\" "
                + "name=\"deserialize\"><arguments>"
                + "<argument>StoreableTable[" + sandBoxDirectory + "/testTable]</argument>"
                + "<argument>[4, false, \"fourth\"]</argument></arguments>"
                + "<return>StoreableRow[4,false,fourth]</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void serializeProxyTest() throws ParseException {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        ((TableProvider) testProxy).serialize(testTable, testRow);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider\" "
                + "name=\"serialize\"><arguments><argument>StoreableTable[" + sandBoxDirectory + "/testTable]"
                + "</argument><argument>StoreableRow[0,true,]</argument></arguments>"
                + "<return>[0,true,null]</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void createFor1ProxyTest() throws ParseException {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        ((TableProvider) testProxy).createFor(testTable);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider\" "
                + "name=\"createFor\"><arguments><argument>StoreableTable["
                + sandBoxDirectory + "/testTable]</argument>"
                + "</arguments><return>StoreableRow[,,]</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void createFor2ProxyTest() throws ParseException {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        List<Object> tempListWithValues = new ArrayList<>();
        tempListWithValues.add(1);
        tempListWithValues.add(true);
        tempListWithValues.add("Hello World!");
        ((TableProvider) testProxy).createFor(testTable, tempListWithValues);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider\" "
                + "name=\"createFor\"><arguments><argument>StoreableTable[" + sandBoxDirectory
                + "/testTable]</argument>"
                + "<argument><list><value>1</value><value>true</value><value>Hello World!</value></list></argument>"
                + "</arguments><return>StoreableRow[1,true,Hello World!]</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void tableProviderToStringNotProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        testProxy.toString();
        Assert.assertEquals("", writer.toString());
    }

    @Test
    public void tableProviderCodeNotProxyTest() {
        testProxy = testProxyLoggingFactory.wrap(writer, testProvider, TableProvider.class);
        testProxy.hashCode();
        Assert.assertEquals("", writer.toString());
    }

    /**
     * TEST BLOCK
     * TABLE PROVIDER FACTORY INTERFACE TESTS
     */

    @Test
    public void createTableProviderProxyTest() throws IOException {
        testProxy = testProxyLoggingFactory.wrap(writer, testFactory, TableProviderFactory.class);
        ((TableProviderFactory) testProxy).create(sandBoxDirectory + "/new");
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable"
                + ".StoreableTableProviderFactory\" name=\"create\"><arguments>"
                + "<argument>" + sandBoxDirectory + "/new</argument></arguments><return>"
                + "StoreableTableProvider[" + sandBoxDirectory + "/new]</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }
}
