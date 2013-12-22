package ru.fizteh.fivt.students.anastasyev.proxy.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.anastasyev.proxy.MyLoggingProxyFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class MyLoggingProxyFactoryTest {
    private Writer logWriter;
    private LoggingProxyFactory proxyFactory;
    private XMLStreamWriter xmlStreamWriter;
    private Writer expectedWriter;
    private Impl impl;

    public String timeStamp(String str) {
        int first = str.indexOf("\"");
        int last = str.indexOf("\" class");
        return str.substring(first + 1, last);
    }

    @Before
    public void init() throws XMLStreamException {
        logWriter = new StringWriter();
        expectedWriter = new StringWriter();
        proxyFactory = new MyLoggingProxyFactory();
        impl = new Impl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullImpl() {
        proxyFactory.wrap(new StringWriter(), null, List.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullWriter() {
        proxyFactory.wrap(null, new ArrayList<>(), List.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInterface() {
        proxyFactory.wrap(new StringWriter(), new ArrayList<>(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongImplementation() {
        proxyFactory.wrap(new StringWriter(), new HashMap<>(), Iterable.class);
    }

    @Test
    public void testSimpleRun() throws XMLStreamException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        MyInterface proxy = (MyInterface) proxyFactory.wrap(logWriter, impl, MyInterface.class);
        proxy.run();

        xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(expectedWriter);

        xmlStreamWriter.writeStartElement("invoke");
        xmlStreamWriter.writeAttribute("timestamp", timeStamp(logWriter.toString()));
        xmlStreamWriter.writeAttribute("class", impl.getClass().getName());
        xmlStreamWriter.writeAttribute("name", "run");

        xmlStreamWriter.writeEmptyElement("arguments");

        xmlStreamWriter.writeStartElement("return");
        xmlStreamWriter.writeCharacters("Run!");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();

        assertEquals(expectedWriter.toString() + System.getProperty("line.separator"), logWriter.toString());
    }

    @Test
    public void testRunWithArgs() throws XMLStreamException {
        MyInterface proxy = (MyInterface) proxyFactory.wrap(logWriter, impl, MyInterface.class);
        List list = new ArrayList<String>();
        list.add("First string");
        list.add("Second string");
        proxy.runWithArguments("String", 2, list);

        xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(expectedWriter);

        xmlStreamWriter.writeStartElement("invoke");
        xmlStreamWriter.writeAttribute("timestamp", timeStamp(logWriter.toString()));
        xmlStreamWriter.writeAttribute("class", impl.getClass().getName());
        xmlStreamWriter.writeAttribute("name", "runWithArguments");

        xmlStreamWriter.writeStartElement("arguments");

        xmlStreamWriter.writeStartElement("argument");
        xmlStreamWriter.writeCharacters("String");
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeStartElement("argument");
        xmlStreamWriter.writeCharacters("2");
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeStartElement("argument");
        xmlStreamWriter.writeStartElement("list");
        xmlStreamWriter.writeStartElement("value");
        xmlStreamWriter.writeCharacters("First string");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeStartElement("value");
        xmlStreamWriter.writeCharacters("Second string");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeStartElement("return");
        xmlStreamWriter.writeCharacters("Run anyway?");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();

        assertEquals(expectedWriter.toString() + System.getProperty("line.separator"), logWriter.toString());
    }

    @Test
    public void testRunWithCycle() throws XMLStreamException {
        MyInterface proxy = (MyInterface) proxyFactory.wrap(logWriter, impl, MyInterface.class);
        List list = new ArrayList<Object>();
        list.add(list);
        proxy.runCycle(list);

        xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(expectedWriter);

        xmlStreamWriter.writeStartElement("invoke");
        xmlStreamWriter.writeAttribute("timestamp", timeStamp(logWriter.toString()));
        xmlStreamWriter.writeAttribute("class", impl.getClass().getName());
        xmlStreamWriter.writeAttribute("name", "runCycle");

        xmlStreamWriter.writeStartElement("arguments");

        xmlStreamWriter.writeStartElement("argument");

        xmlStreamWriter.writeStartElement("list");
        xmlStreamWriter.writeStartElement("value");
        xmlStreamWriter.writeCharacters("cyclic");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeStartElement("return");
        xmlStreamWriter.writeEmptyElement("null");
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();

        assertEquals(expectedWriter.toString() + System.getProperty("line.separator"), logWriter.toString());
    }

    @Test
    public void testRunWithCycleNested() throws XMLStreamException {
        MyInterface proxy = (MyInterface) proxyFactory.wrap(logWriter, impl, MyInterface.class);
        List<Object> list = new ArrayList<>();
        list.add(null);
        ArrayList<Object> list2 = new ArrayList<>();
        list.add(list2);
        list2.add(list);
        proxy.runCycle(list);

        xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(expectedWriter);

        xmlStreamWriter.writeStartElement("invoke");
        xmlStreamWriter.writeAttribute("timestamp", timeStamp(logWriter.toString()));
        xmlStreamWriter.writeAttribute("class", impl.getClass().getName());
        xmlStreamWriter.writeAttribute("name", "runCycle");

        xmlStreamWriter.writeStartElement("arguments");


        xmlStreamWriter.writeStartElement("argument");

        xmlStreamWriter.writeStartElement("list");

        xmlStreamWriter.writeStartElement("value");
        xmlStreamWriter.writeEmptyElement("null");
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeStartElement("value");
        xmlStreamWriter.writeStartElement("list");
        xmlStreamWriter.writeStartElement("value");
        xmlStreamWriter.writeCharacters("cyclic");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();


        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeStartElement("return");
        xmlStreamWriter.writeEmptyElement("null");
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();

        assertEquals(expectedWriter.toString() + System.getProperty("line.separator"), logWriter.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testRunThrowable() throws XMLStreamException {
        MyInterface proxy = (MyInterface) proxyFactory.wrap(logWriter, impl, MyInterface.class);
        List list = new ArrayList<String>();
        list.add("First string");
        list.add("Second string");
        try {
            proxy.runThrowable("String", 2, list);
        } finally {
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(expectedWriter);

            xmlStreamWriter.writeStartElement("invoke");
            xmlStreamWriter.writeAttribute("timestamp", timeStamp(logWriter.toString()));
            xmlStreamWriter.writeAttribute("class", impl.getClass().getName());
            xmlStreamWriter.writeAttribute("name", "runThrowable");

            xmlStreamWriter.writeStartElement("arguments");

            xmlStreamWriter.writeStartElement("argument");
            xmlStreamWriter.writeCharacters("String");
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeStartElement("argument");
            xmlStreamWriter.writeCharacters("2");
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeStartElement("argument");
            xmlStreamWriter.writeStartElement("list");
            xmlStreamWriter.writeStartElement("value");
            xmlStreamWriter.writeCharacters("First string");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeStartElement("value");
            xmlStreamWriter.writeCharacters("Second string");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeStartElement("thrown");
            xmlStreamWriter.writeCharacters("java.lang.IllegalStateException: Do not run. :(");
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.flush();

            assertEquals(expectedWriter.toString() + System.getProperty("line.separator"), logWriter.toString());
        }
    }
}
