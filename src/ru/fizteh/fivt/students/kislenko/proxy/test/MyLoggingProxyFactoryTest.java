package ru.fizteh.fivt.students.kislenko.proxy.test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.kislenko.proxy.MyLoggingProxyFactory;
import ru.fizteh.fivt.students.kislenko.proxy.MyTableProviderFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MyLoggingProxyFactoryTest {
    static StringWriter writer;
    static LoggingProxyFactory factory = new MyLoggingProxyFactory();

    @Before
    public void setUp() throws Exception {
        writer = new StringWriter();
    }

    @Test
    public void testMyTableProviderFactoryToString() throws Exception {
        MyTableProviderFactory f = new MyTableProviderFactory();
        Object o = factory.wrap(writer, f, TableProviderFactory.class);
        o.toString();

        Assert.assertEquals("", writer.getBuffer().toString());
    }

    @Test
    public void testListClear() throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(0);
        list.add(null);
        list.add("Monitor");
        List o = (List) factory.wrap(writer, list, List.class);
        o.clear();

        String result = writer.getBuffer().toString();
        Assert.assertTrue(result.matches("<invoke timestamp=\"[0-9]{13}\" class=\"java.util.ArrayList\" name=\"clear\">"
                + "<arguments/></invoke>\n"));
    }

    @Test
    public void testListCyclic() throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(list);
        List o = (List) factory.wrap(writer, list, List.class);
        o.add(list);

        String result = writer.getBuffer().toString();
        Assert.assertTrue(result.matches("<invoke timestamp=\"[0-9]{13}\" class=\"java.util.ArrayList\" name=\"add\">"
                + "<arguments><argument><list><value><list><value>cyclic</value></list></value></list></argument>"
                + "</arguments><return>true</return></invoke>\n"));
    }

    @Test
    public void testWrap() throws Exception {
        MyLoggingProxyFactory proxyFactory = new MyLoggingProxyFactory();
        LoggingProxyFactory o = (LoggingProxyFactory) factory.wrap(writer, proxyFactory, LoggingProxyFactory.class);
        o.wrap(writer, proxyFactory, LoggingProxyFactory.class);

        String result = writer.getBuffer().toString();
        Assert.assertTrue(result.matches("<invoke timestamp=\"[0-9]{13}\" class=\"ru.fizteh.fivt.students.kislenko.prox"
                + "y.MyLoggingProxyFactory\" name=\"wrap\"><arguments><argument>&lt;invoke timestamp=\"[0-9]{13}\" clas"
                + "s=\"ru.fizteh.fivt.students.kislenko.proxy.MyLoggingProxyFactory\" name=\"wrap\"&gt;&lt;arguments&gt"
                + ";&lt;argument</argument><argument>ru.fizteh.fivt.students.kislenko.proxy.MyLoggingProxyFactory@"
                + "[0-9a-f]+</argument><argument>interface ru.fizteh.fivt.proxy.LoggingProxyFactory</argument></argumen"
                + "ts><return>ru.fizteh.fivt.students.kislenko.proxy.MyLoggingProxyFactory@[0-9a-zA-Z]+</return></invok"
                + "e>\n"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() throws Exception {
        MyLoggingProxyFactory proxyFactory = new MyLoggingProxyFactory();
        LoggingProxyFactory o = (LoggingProxyFactory) factory.wrap(writer, proxyFactory, LoggingProxyFactory.class);
        o.wrap(null, null, null);
        System.out.println(writer.getBuffer().toString());
    }
}
