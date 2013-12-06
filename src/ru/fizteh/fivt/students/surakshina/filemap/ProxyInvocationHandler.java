package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ProxyInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;
    private XMLStreamWriter xmlWriter;

    public ProxyInvocationHandler(Writer newWriter, Object newImplementation) {
        writer = newWriter;
        implementation = newImplementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(implementation, args);
        }
        Object value = null;
        Throwable exception = null;
        try {
            value = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
            throw exception;
        } finally {
            try {
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                StringWriter writerString = new StringWriter();
                xmlWriter = factory.createXMLStreamWriter(writerString);
                buildLog(method, args, exception, value);
                xmlWriter.writeEndElement();
                writer.write(writerString.toString());
                writer.write(System.lineSeparator());
            } catch (Throwable e) {
                // it is ok
            }
        }
        return value;

    }

    private void buildLog(Method method, Object[] args, Throwable exception, Object value) {
        try {
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
            xmlWriter.writeAttribute("class", implementation.getClass().getName());
            xmlWriter.writeAttribute("name", method.getName());
            if (args == null || args.length == 0) {
                xmlWriter.writeEmptyElement("arguments");
            } else {
                xmlWriter.writeStartElement("arguments");
                for (Object object : args) {
                    writeArgument(object);
                }
                xmlWriter.writeEndElement();
            }
            if (exception != null) {
                xmlWriter.writeStartElement("thrown");
                xmlWriter.writeCharacters(exception.getClass().getName() + ": " + exception.getMessage());
                xmlWriter.writeEndElement();
            } else {
                if (!method.getReturnType().equals(void.class)) {
                    xmlWriter.writeStartElement("return");
                    if (value != null) {
                        xmlWriter.writeCharacters(value.toString());
                    } else {
                        xmlWriter.writeEmptyElement("null");
                    }
                    xmlWriter.writeEndElement();
                }
            }
        } catch (Throwable e) {
            // it is ok
        }
    }

    private void writeArgument(Object object) throws XMLStreamException {
        xmlWriter.writeStartElement("argument");
        if (object == null) {
            xmlWriter.writeEmptyElement("null");
        } else if (object instanceof Iterable) {
            writeList((Iterable<?>) object, new IdentityHashMap<Object, Boolean>());
        } else {
            xmlWriter.writeCharacters(object.toString());
        }
        xmlWriter.writeEndElement();
    }

    private void writeList(Iterable<?> object, IdentityHashMap<Object, Boolean> map) throws XMLStreamException {
        xmlWriter.writeStartElement("list");
        for (Object value : object) {
            xmlWriter.writeStartElement("value");
            if (value == null) {
                xmlWriter.writeEmptyElement("null");
            } else {
                if (value instanceof Iterable) {
                    if (map.containsKey(value) && ((Iterable<?>) value).iterator().hasNext()) {
                        xmlWriter.writeCharacters("cyclic");
                    } else {
                        map.put(value, true);
                        writeList((Iterable<?>) value, map);
                    }
                } else {
                    xmlWriter.writeCharacters(value.toString());
                }
            }
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();

    }
}
