package ru.fizteh.fivt.students.inaumov.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

public class XMLFormatter {
    private final StringWriter stringWriter = new StringWriter();
    private final XMLStreamWriter xmlStreamWriter;
    private final IdentityHashMap<Object, Boolean> identityHashMap = new IdentityHashMap<Object, Boolean>();

    public XMLFormatter() throws XMLStreamException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
    }

    public void writeMethodLog(Method method, Object[] args, Object implementation,
                               Throwable throwable, Object retValue) throws IOException {
        try {
            xmlStreamWriter.writeStartElement("invoke");
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }

        writeTimeStamp();
        writeMethod(method);
        writeClass(implementation.getClass());
        writeArguments(args);

        if (throwable != null) {
            writeThrown(throwable);
        } else {
            if (method.getReturnType() != Void.TYPE) {
                writeReturnValue(retValue);
            }
        }

        try {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }

    public void writeTimeStamp() throws IOException {
        try {
            xmlStreamWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void writeClass(Class<?> clazz) throws IOException {
        try {
            xmlStreamWriter.writeAttribute("class", clazz.getName());
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void writeMethod(Method method) throws IOException {
        try {
            xmlStreamWriter.writeAttribute("name", method.getName());
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void writeArguments(Object[] args) throws IOException {
        try {
            xmlStreamWriter.writeStartElement("arguments");
            if (args != null) {
                for (final Object object : args) {
                    xmlStreamWriter.writeStartElement("argument");

                    if (object == null) {
                        writeNull();
                    } else if (object instanceof Iterable) {
                        identityHashMap.clear();
                        writeIterable((Iterable) object);
                    } else {
                        writeObject(object);
                    }

                    xmlStreamWriter.writeEndElement();
                }
            }

            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void writeReturnValue(Object returnValue) throws IOException {
        try {
            xmlStreamWriter.writeStartElement("return");
            if (returnValue == null) {
                writeNull();
            } else {
                xmlStreamWriter.writeCharacters(returnValue.toString());
            }

            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void writeThrown(Throwable throwable) throws IOException {
        try {
            xmlStreamWriter.writeStartElement("thrown");
            xmlStreamWriter.writeCharacters(throwable.toString());
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void writeNull() throws XMLStreamException {
        xmlStreamWriter.writeStartElement("null");
        xmlStreamWriter.writeEndElement();
    }

    private void writeIterable(Iterable iterable) throws XMLStreamException {
        for (final Object object: iterable) {
            if (object instanceof Iterable) {
                if (identityHashMap.containsKey(object)) {
                    xmlStreamWriter.writeCharacters("cyclic");
                    return;
                }
            }
        }
        xmlStreamWriter.writeStartElement("list");

        for (final Object object : iterable) {

            xmlStreamWriter.writeStartElement("value");

            if (object == null) {
                writeNull();
            } else if (object instanceof Iterable) {
                if (identityHashMap.put(object, true) != null) {
                    xmlStreamWriter.writeCharacters("cyclic");
                } else {
                    writeIterable((Iterable) object);
                    identityHashMap.remove(object);
                }
            } else {
                identityHashMap.put(object, true);
                writeObject(object);
            }

            xmlStreamWriter.writeEndElement();
        }

        xmlStreamWriter.writeEndElement();
    }

    private void writeObject(Object object) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(object.toString());
    }
}
