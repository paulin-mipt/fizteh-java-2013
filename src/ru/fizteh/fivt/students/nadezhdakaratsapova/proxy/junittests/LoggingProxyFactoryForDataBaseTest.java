package ru.fizteh.fivt.students.nadezhdakaratsapova.proxy.junittests;

import junit.framework.Assert;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.proxy.LoggingProxyFactoryForDataBase;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableDataValue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggingProxyFactoryForDataBaseTest {

    private LoggingProxyFactoryForDataBase loggingProxyFactory = new LoggingProxyFactoryForDataBase();
    private StringWriter writer = new StringWriter();

    @Test
    public void wrapGetKey() throws Exception {
        Map<String, Integer> storage = new HashMap<String, Integer>();
        Map<String, Integer> proxyStorage = (Map<String, Integer>) loggingProxyFactory.wrap(writer, storage, Map.class);
        proxyStorage.get("key");
        StringBuilder result = new StringBuilder(writer.toString());
        Assert.assertEquals(result.substring(31, result.length()).toString(), new String(
                "\n  \"arguments\": [\"key\"],\n"
                        + "  \"returnValue\": null,\n"
                        + "  \"class\": \"java.util.HashMap\",\n"
                        + "  \"method\": \"get\"\n"
                        + "}\n"));
    }


    @Test
    public void charAtLogThrowable() throws Exception {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable storeableDataValue = new StoreableDataValue(types);
        Storeable proxyStoreable = (Storeable) loggingProxyFactory.wrap(writer, storeableDataValue, Storeable.class);
        try {
            proxyStoreable.setColumnAt(5, 5);
        } catch (IndexOutOfBoundsException e) {
            StringBuilder result = new StringBuilder(writer.toString());
            Assert.assertEquals(new String(
                    "\n  \"thrown\": \"java.lang.IndexOutOfBoundsException: Invalid index of column\","
                            + "\n  \"arguments\": [\n    5,\n    5\n  ],\n"
                            + "  \"class\": "
                            + "\"ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableDataValue\",\n"
                            + "  \"method\": \"setColumnAt\"\n"
                            + "}\n"), result.substring(31, result.length()).toString());
        }
    }

    @Test
    public void cyclicReference() throws Exception {
        List<Object> list = new ArrayList<>();
        list.add(list);
        List<Object> proxyList = (List<Object>) loggingProxyFactory.wrap(writer, list, List.class);
        proxyList.add(list);
        StringBuilder result = new StringBuilder(writer.toString());
        Assert.assertEquals(result.substring(31, result.length()).toString(), new String(
                "\n  \"arguments\": [[\"cyclic\"]],\n"
                        + "  \"returnValue\": true,\n"
                        + "  \"class\": \"java.util.ArrayList\",\n"
                        + "  \"method\": \"add\"\n"
                        + "}\n"));
    }
}
