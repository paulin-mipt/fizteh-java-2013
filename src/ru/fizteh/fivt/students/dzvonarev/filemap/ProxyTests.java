package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProxyTests {

    private ProxyTestInterface testInterface;
    private StringWriter xmlWriter;

    @Before
    public void test() {
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        xmlWriter = new StringWriter();
        ProxyTestImplement implementProxy = new ProxyTestImplement();
        testInterface = (ProxyTestInterface) factory.wrap(xmlWriter, implementProxy, ProxyTestInterface.class);
    }

    private String getStringFromXml() {
        String[] str = xmlWriter.toString().split("\\s");
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 2; i < str.length; ++i) {
            strBuilder.append(str[i]);
            strBuilder.append(" ");
        }
        strBuilder.delete(strBuilder.length() - 10, strBuilder.length());
        return strBuilder.toString();
    }

    @Test
    public void testVoidAction() {
        testInterface.voidAction();
        String str = getStringFromXml();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.dzvonarev.filemap.ProxyTestImplement\""
                + " name=\"voidAction\"><arguments/>");
    }

    @Test
    public void testGetInteger() throws Exception {
        testInterface.getInteger(125);
        String str = getStringFromXml();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.dzvonarev.filemap.ProxyTestImplement\" "
                + "name=\"getInteger\"><arguments><argument>125</argument></arguments>"
                + "<return>125</return>");

    }

    @Test
    public void testGetIntegerFromIterable() {
        List<Integer> list = new ArrayList<>();
        list.add(1023);
        testInterface.getIntFromIterable(list);
        String str = getStringFromXml();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.dzvonarev.filemap.ProxyTestImplement\" "
                + "name=\"getIntFromIterable\"><arguments><argument>"
                + "<list><value>1023</value></list></argument></arguments><return>1023</return>");
    }

    @Test(expected = Exception.class)
    public void testThrowExceptionAction() throws Exception {
        testInterface.throwExceptionAction();
        String str = getStringFromXml();
        assertEquals(str, "class=\"ru.fizteh.fivt.students.dzvonarev.filemap.ProxyTestImplement\""
                + " name=\"throwExceptionAction\"><arguments/>"
                + "<thrown>java.lang.Exception: exception throw success</thrown>");
    }

    @After
    public void afterTest() throws IOException {
        xmlWriter.close();
    }

}
