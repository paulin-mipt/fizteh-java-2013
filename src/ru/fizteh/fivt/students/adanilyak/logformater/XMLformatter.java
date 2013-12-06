package ru.fizteh.fivt.students.adanilyak.logformater;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

/**
 * User: Alexander
 * Date: 22.11.13
 * Time: 22:48
 */

public class XMLformatter implements Closeable {
    private StringWriter stringWriter = new StringWriter();
    private XMLStreamWriter xmlStreamWriter = null;
    private final IdentityHashMap<Object, Boolean> forCycleLinkSearch = new IdentityHashMap<>();

    public XMLformatter() throws IOException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try {
            xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
            xmlStreamWriter.writeStartElement("invoke");
        } catch (XMLStreamException exc) {
            throw new IOException("xml formatter creation error: " + exc.getMessage());
        }
    }

    public void writeTimeStamp() throws IOException {
        try {
            xmlStreamWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        } catch (XMLStreamException exc) {
            throw new IOException("xml write timestamp error: " + exc.getMessage());
        }
    }

    public void writeClass(Class<?> clazz) throws IOException {
        try {
            xmlStreamWriter.writeAttribute("class", clazz.getName());
        } catch (XMLStreamException exc) {
            throw new IOException("xml write class error: " + exc.getMessage());
        }
    }

    public void writeMethod(Method method) throws IOException {
        try {
            xmlStreamWriter.writeAttribute("name", method.getName());
        } catch (XMLStreamException exc) {
            throw new IOException("xml write method name error: " + exc.getMessage());
        }
    }

    public void writeArguments(Object[] args) throws IOException {
        try {
            xmlStreamWriter.writeStartElement("arguments");
            if (args != null) {
                recursivePart(Arrays.asList(args), xmlStreamWriter, false, false);
            }
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException exc) {
            throw new IOException("xml write arguments error: " + exc.getMessage());
        }
        forCycleLinkSearch.clear();
    }

    private void recursivePart(Iterable collection, XMLStreamWriter xmlStreamWriter, boolean inList, boolean inCycle)
            throws XMLStreamException {
        boolean isContainer;
        boolean isEmpty;
        for (Object value : collection) {
            if (value == null) {
                if (inList) {
                    xmlStreamWriter.writeStartElement("value");
                } else {
                    xmlStreamWriter.writeStartElement("argument");
                }
                xmlStreamWriter.writeStartElement("null");
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndElement();
                continue;
            }

            if (value.getClass().isArray()) {
                if (inList) {
                    xmlStreamWriter.writeStartElement("value");
                } else {
                    xmlStreamWriter.writeStartElement("argument");
                }
                xmlStreamWriter.writeCharacters(value.toString());
                xmlStreamWriter.writeEndElement();
                continue;
            }

            // Check value, simple or iterable?
            isContainer = false;
            isEmpty = false;

            if (value instanceof Iterable) {
                isContainer = true;
                isEmpty = !((Iterable) value).iterator().hasNext();
            }

            if (forCycleLinkSearch.containsKey(value) && isContainer && !isEmpty) {
                inCycle = true;
            }
            forCycleLinkSearch.put(value, true);
            if (isContainer) {
                if (!inCycle && !inList) {
                    xmlStreamWriter.writeStartElement("argument");
                } else {
                    xmlStreamWriter.writeStartElement("value");
                }
                xmlStreamWriter.writeStartElement("list");
                inList = true;
                if (!inCycle) {
                    recursivePart((Iterable) value, xmlStreamWriter, inList, inCycle);
                    xmlStreamWriter.writeEndElement();
                } else {
                    for (Object inside : (Iterable) value) {
                        if (inside == null) {
                            xmlStreamWriter.writeStartElement("value");
                            xmlStreamWriter.writeStartElement("null");
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeEndElement();
                            continue;
                        }
                        if (inside instanceof Iterable) {
                            xmlStreamWriter.writeStartElement("value");
                            xmlStreamWriter.writeCharacters("cyclic");
                            xmlStreamWriter.writeEndElement();
                            continue;
                        }
                        xmlStreamWriter.writeStartElement("value");
                        xmlStreamWriter.writeCharacters(inside.toString());
                        xmlStreamWriter.writeEndElement();
                    }
                    xmlStreamWriter.writeEndElement();
                    inCycle = false;
                }
                xmlStreamWriter.writeEndElement();
                inList = false;
                continue;
            }
            if (inList) {
                xmlStreamWriter.writeStartElement("value");
            } else {
                xmlStreamWriter.writeStartElement("argument");
            }
            xmlStreamWriter.writeCharacters(value.toString());
            xmlStreamWriter.writeEndElement();
        }
    }

    public void writeReturnValue(Object result) throws IOException {
        try {
            xmlStreamWriter.writeStartElement("return");
            if (result != null) {
                xmlStreamWriter.writeCharacters(result.toString());
            } else {
                xmlStreamWriter.writeStartElement("null");
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException exc) {
            throw new IOException("xml write return value error: " + exc.getMessage());
        }
    }

    public void writeThrown(Throwable throwable) throws IOException {
        try {
            xmlStreamWriter.writeStartElement("thrown");
            xmlStreamWriter.writeCharacters(throwable.toString());
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException exc) {
            throw new IOException("xml write thrown error: " + exc.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        try {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.flush();
        } catch (XMLStreamException exc) {
            throw new IOException("xml stream writer close error: " + exc.getMessage());
        }
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }
}
