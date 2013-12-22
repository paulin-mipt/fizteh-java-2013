package ru.fizteh.fivt.students.irinaGoltsman.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.io.StringWriter;

public class ProxyInvocationHandler implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;
    private Throwable exception = null;
    private XMLStreamWriter xmlWriter;
    private IdentityHashMap<Object, Boolean> checkForCircularReferences = new IdentityHashMap<>();

    public ProxyInvocationHandler(Writer inputWriter, Object object) {
        writer = inputWriter;
        implementation = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            result = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
        } catch (Throwable exc) {
            exception = exc;
        }
        if (method.getDeclaringClass().equals(Object.class)) {
            if (exception != null) {
                throw exception;
            }
            return result;
        }
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            StringWriter stringWriter = new StringWriter();
            xmlWriter = factory.createXMLStreamWriter(stringWriter);
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
            xmlWriter.writeAttribute("class", implementation.getClass().getName());
            xmlWriter.writeAttribute("name", method.getName());
            writeArguments(args);
            if (exception != null) {
                xmlWriter.writeStartElement("thrown");
                xmlWriter.writeCharacters(exception.toString());
                xmlWriter.writeEndElement();
            } else {
                if (!method.getReturnType().equals(void.class)) {
                    xmlWriter.writeStartElement("return");
                    if (result == null) {
                        xmlWriter.writeEmptyElement("null");
                    } else {
                        xmlWriter.writeCharacters(result.toString());
                    }
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();
            xmlWriter.writeCharacters("\n");
            xmlWriter.close();
            writer.write(stringWriter.toString());
            stringWriter.close();
        } catch (Throwable e) {
            //
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    private void writeArguments(Object[] args) throws XMLStreamException {
        if (args != null) {
            if (args.length == 0) {
                xmlWriter.writeEmptyElement("arguments");
            } else {
                xmlWriter.writeStartElement("arguments");
                for (Object argument : args) {
                    xmlWriter.writeStartElement("argument");
                    if (argument == null) {
                        xmlWriter.writeEmptyElement("null");
                    } else {
                        if (argument instanceof Iterable) {
                            checkForCircularReferences.put(argument, true);
                            recursiveWritingList((Iterable<?>) argument);
                        } else {
                            xmlWriter.writeCharacters(argument.toString());
                        }
                    }
                    xmlWriter.writeEndElement();
                }
                xmlWriter.writeEndElement();
            }
        } else {
            xmlWriter.writeEmptyElement("arguments");
        }
    }

    private void recursiveWritingList(Iterable<?> values) throws XMLStreamException {
        if (!values.iterator().hasNext()) {
            xmlWriter.writeEmptyElement("list");
        } else {
            xmlWriter.writeStartElement("list");
            for (Object value : values) {
                xmlWriter.writeStartElement("value");
                if (value == null) {
                    xmlWriter.writeEmptyElement("null");
                } else {
                    if (value instanceof Iterable) {
                        if (checkForCircularReferences.put(value, true) != null) {
                            //Значит нашли циклическую ссылку
                            xmlWriter.writeCharacters("cyclic");
                        } else {
                            recursiveWritingList((Iterable<?>) value);
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
}
