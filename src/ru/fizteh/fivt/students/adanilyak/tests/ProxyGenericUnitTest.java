package ru.fizteh.fivt.students.adanilyak.tests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.adanilyak.proxy.ProxyLoggingFactoryImplementation;
import ru.fizteh.fivt.students.adanilyak.tools.StringCreationTools;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexander
 * Date: 30.11.13
 * Time: 3:04
 */
public class ProxyGenericUnitTest {
    ProxyLoggingFactoryImplementation testProxyLoggingFactory;
    StringWriter writer;
    ImplementationForProxyTest impl;

    @Before
    public void setUp() {
        writer = new StringWriter();
        testProxyLoggingFactory = new ProxyLoggingFactoryImplementation();
        impl = new ImplementationForProxyTest();
    }

    @Test
    public void methodWithOutArgsTest() {
        InterfaceForProxyTest testProxy
                = (InterfaceForProxyTest) testProxyLoggingFactory.wrap(writer, impl, InterfaceForProxyTest.class);
        testProxy.methodWithOutArgs();
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.tests.ImplementationForProxyTest\" "
                + "name=\"methodWithOutArgs\"><arguments></arguments>"
                + "<return>methodWithOutArgs result</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void methodMixedArgsTest() {
        InterfaceForProxyTest testProxy
                = (InterfaceForProxyTest) testProxyLoggingFactory.wrap(writer, impl, InterfaceForProxyTest.class);
        Integer[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        list.add("0");
        testProxy.methodMixedArgs("Hello World!", data, list);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.tests.ImplementationForProxyTest\" "
                + "name=\"methodMixedArgs\"><arguments><argument>Hello World!</argument>"
                + "<argument>" + data.toString() + "</argument><argument><list><value>1</value><value>2</value>"
                + "<value>3</value><value>4</value><value>5</value><value>6</value><value>7</value><value>8</value>"
                + "<value>9</value><value>0</value></list></argument></arguments>"
                + "<return>methodMixedArgs result</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void methodWithCycleReferences() {
        InterfaceForProxyTest testProxy
                = (InterfaceForProxyTest) testProxyLoggingFactory.wrap(writer, impl, InterfaceForProxyTest.class);
        List<Object> list = new ArrayList<>();
        list.add(null);
        list.add(list);
        testProxy.methodWithCycleReferences(list);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.tests.ImplementationForProxyTest\" "
                + "name=\"methodWithCycleReferences\"><arguments><argument><list><value><null></null></value><value>"
                + "<list><value><null></null></value>"
                + "<value>cyclic</value></list></value></list></argument></arguments>"
                + "<return>[null, (this Collection)]</return></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }

    @Test
    public void methodThrowsException() {
        InterfaceForProxyTest testProxy
                = (InterfaceForProxyTest) testProxyLoggingFactory.wrap(writer, impl, InterfaceForProxyTest.class);
        Integer data = 1;
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        list.add("0");
        try {
            testProxy.methodThrowsException("Hello World!", data, list);
        } catch (Exception ignored) {
            // exception is ok!
        }
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.tests.ImplementationForProxyTest\" "
                + "name=\"methodThrowsException\"><arguments><argument>Hello World!</argument><argument>1</argument>"
                + "<argument><list><value>1</value><value>2</value><value>3</value><value>4</value><value>5</value>"
                + "<value>6</value><value>7</value><value>8</value><value>9</value><value>0</value></list></argument>"
                + "</arguments><thrown>java.lang."
                + "IllegalStateException: implementation method throws exception: ok!</thrown></invoke>",
                StringCreationTools.cutTimeStamp(writer.toString()));
    }
}
