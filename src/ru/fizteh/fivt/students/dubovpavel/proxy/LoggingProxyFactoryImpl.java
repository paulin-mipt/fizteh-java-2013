package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;

public class LoggingProxyFactoryImpl implements LoggingProxyFactory {
    public Object wrap(final Writer writer, final Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer is null");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("Implementation is null");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class is null");
        }
        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("Implementation does not implement interface");
        }
        InvocationHandler handler = new InvocationHandler() {
            private void buildArgumentsTree(XMLStreamWriter xmlWriter, Iterable args,
                                            IdentityHashMap<Object, Iterable> finishedArguments, int level)
                    throws XMLStreamException {
                for (Iterator i = args.iterator(); i.hasNext(); ) {
                    Object next = i.next();
                    xmlWriter.writeStartElement(level == 0 ? "argument" : "value");
                    if (finishedArguments.containsKey(next)) {
                        xmlWriter.writeCharacters("cyclic");
                    } else {
                        finishedArguments.put(next, args);
                        if (next instanceof Iterable) {
                            xmlWriter.writeStartElement("list");
                            buildArgumentsTree(xmlWriter, (Iterable) next, finishedArguments, level + 1);
                            xmlWriter.writeEndElement();
                        } else {
                            if (next == null) {
                                xmlWriter.writeEmptyElement("null");
                            } else {
                                xmlWriter.writeCharacters(next.toString());
                            }
                        }
                        finishedArguments.remove(next);
                    }
                    xmlWriter.writeEndElement();
                }
            }

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass().equals(Object.class)) {
                    return method.invoke(implementation, args);
                }
                StringWriter buffer = new StringWriter();
                XMLStreamWriter xmlWriter = null;
                boolean xmlBuilt = true;
                try {
                    xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(buffer);
                    xmlWriter.writeStartElement("invoke");
                    xmlWriter.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
                    xmlWriter.writeAttribute("class", implementation.getClass().getName());
                    xmlWriter.writeAttribute("name", method.getName());
                    if (args == null || args.length == 0) {
                        xmlWriter.writeEmptyElement("arguments");
                    } else {
                        xmlWriter.writeStartElement("arguments");
                        buildArgumentsTree(xmlWriter, Arrays.asList(args), new IdentityHashMap<Object, Iterable>(), 0);
                        xmlWriter.writeEndElement();
                    }

                } catch (XMLStreamException xmlE) {
                    xmlBuilt = false;
                }
                boolean thrown = false;
                Object ret = null;
                try {
                    ret = method.invoke(implementation, args);
                } catch (InvocationTargetException e) {
                    thrown = true;
                    try {
                        xmlWriter.writeStartElement("thrown");
                        xmlWriter.writeCharacters(e.getTargetException().toString());
                        xmlWriter.writeEndElement();
                    } catch (XMLStreamException xmlE) {
                        xmlBuilt = false;
                    }
                    throw e.getTargetException();
                } finally {
                    if (!thrown && !method.getReturnType().equals(Void.TYPE)) {
                        try {
                            xmlWriter.writeStartElement("return");
                            if (ret == null) {
                                xmlWriter.writeEmptyElement("null");
                            } else {
                                xmlWriter.writeCharacters(ret.toString());
                            }
                            xmlWriter.writeEndElement();
                        } catch (XMLStreamException xmlE) {
                            xmlBuilt = false;
                        }
                    }
                    try {
                        xmlWriter.writeEndElement(); // </invoke>
                        xmlWriter.writeCharacters("\n");
                    } catch (XMLStreamException xmlE) {
                        xmlBuilt = false;
                    }
                    try {
                        if (xmlBuilt) {
                            synchronized (writer) {
                                writer.write(buffer.toString());
                            }
                        }
                    } catch (IOException e) {
                        // According to the task, exception must be ignored
                    }
                }
                return ret;
            }
        };
        return interfaceClass.cast(Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[] {interfaceClass}, handler));
    }
}
