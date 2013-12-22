package ru.fizteh.fivt.students.vlmazlov.proxy.tests;

import org.junit.*;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vlmazlov.proxy.LoggingProxyFactoryImplementation;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyTest {
    private static LoggingProxyFactory factory;

    @BeforeClass
    public static void setUp() {
        factory = new LoggingProxyFactoryImplementation();
    }

    @Test
    public void arrayListIndexOfMethodWithEmptyArrayArgument() {
        ArrayList arrayList = new ArrayList();
        PrintWriter writer = new PrintWriter(System.out);
        List list = null;
        list = (List) factory.wrap(writer, arrayList, List.class);

        try {
            list.indexOf(new Object[]{});
        } finally {
            writer.flush();
        }
    }

    @Test
    public void proxyOfDbTypes() throws Exception {
        List<Class<?>> valueTypes = new ArrayList<Class<?>>() { {
            add(Integer.class);
            add(String.class);
            add(Byte.class);
        }};

        File tempDir = FileUtils.createTempDir("StoreableTableTest", null);
        StoreableTableProvider provider = new StoreableTableProvider(tempDir.getPath(), false);
        StoreableTable storeableTable = provider.createTable("testTable", valueTypes);

        PrintWriter writer = new PrintWriter(System.out);
        Table table = null;
        table = (Table) factory.wrap(writer, storeableTable, Table.class);

        AutoCloseable autoCloseable = null;
        autoCloseable = (AutoCloseable) factory.wrap(writer, storeableTable, AutoCloseable.class);

        try {
            Assert.assertEquals("wrong string representation",
                    "StoreableTable" + "[" + new File(provider.getRoot(), "testTable").getPath() + "]",
                    table.toString());

            Assert.assertNull(table.get("key"));
            autoCloseable.close();
        } finally {
            writer.flush();
        }
    }

    @Test
    public void iterableReturnValue() {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        PrintWriter writer = new PrintWriter(System.out);
        Map<String, Integer> map = null;
        map = (Map<String, Integer>) factory.wrap(writer, hashMap, Map.class);

        map.put("key1", 1);
        map.put("key2", 2);

        try {
            map.entrySet();
        } finally {
            writer.flush();
        }
    }
}
