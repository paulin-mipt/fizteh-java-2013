package ru.fizteh.fivt.students.paulinMatavina.proxy;

import java.io.*;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.students.paulinMatavina.filemap.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProxyTest {
    MyLoggingProxyFactory factory;
    TestInterface wrapped;
    StringWriter writer;
    
    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        factory = new MyLoggingProxyFactory();
        ClassImplementingTestInterface implementation = new ClassImplementingTestInterface();
        wrapped = (TestInterface) factory.wrap(writer, implementation, TestInterface.class);
    }

    @Test(expected = Exception.class)
    public void testIntThrowException() throws Exception {
        wrapped.getIntThrowException(12);
        checkXMLLog("getIntThrowException", "<arguments><argument>12</argument></arguments>"
                + "<thrown>java.lang.RuntimeException: passed int 12</thrown>");
    }
    
    @Test
    public void testTakeStringDoNothing() throws XMLStreamException {
        wrapped.takeStringDoNothing("hi!");
        checkXMLLog("takeStringDoNothing", "<arguments><argument>hi!</argument></arguments>");
    }
    
    @Test
    public void testIterable() throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(null);
        wrapped.getIntFromIterable(list);
        checkXMLLog("getIntFromIterable", "<arguments><argument><list><value><null></null></value>"
                + "</list></argument></arguments><return>42</return>");
    }
    
    @Test(expected = Exception.class)
    public void testJustThrow() throws Exception {
        wrapped.justThrowException();
        checkXMLLog("justThrowException", "<arguments></arguments>" 
        + "<thrown>java.lang.Exception: what if i throw it?</thrown>");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNotImplementingClass() {
        factory.wrap(writer, new MyTableProviderFactory(), TestInterface.class);
    }
    
    @Test
    public void testNewLineSymbol() throws XMLStreamException {
        wrapped.takeStringDoNothing("\n");
        checkXMLLog("takeStringDoNothing", "<arguments><argument>\n</argument></arguments>");
    }
    
    @Test
    public void testIterableCyclic() throws Exception {
        ArrayList<Object> cyclicList = new ArrayList<Object>();
        ArrayList<Object> sublist = new ArrayList<Object>();
        cyclicList.add("begin");
        sublist.add(cyclicList);
        cyclicList.add(sublist);
        cyclicList.add("end");
        wrapped.getIntFromIterable(cyclicList);
        checkXMLLog("getIntFromIterable", "<arguments><argument><list><value>begin</value><value><list><value>" 
                + "<list><value>begin</value><value>cyclic</value><value>end</value></list></value></list>"
                + "</value><value>end</value></list></argument></arguments><return>42</return>");
    }
    
    private void checkXMLLog(String methodName, String exp) throws XMLStreamException {
        String expected = "<invoke timestamp=\"[0-9]*\" name=\"" + methodName 
                    + "\" class=\"ru.fizteh.fivt.students.paulinMatavina.proxy.ClassImplementingTestInterface\">" 
                    + exp + "</invoke>\n";
        if (!writer.toString().matches(expected)) {
            throw new AssertionError("expected " + expected + ", found " + writer.toString());
        }
    }
    
    @After
    public void after() throws IOException {
        writer.close();
    }
}
