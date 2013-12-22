package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.lang.reflect.Proxy;

public class XMLLoggingProxyFactory implements LoggingProxyFactory{

    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Null writer not allowed");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("Null implementation not allowed");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Null doesn't name a class");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("interfaceClass is not an interface");
        }
        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("Object doesn't implement specified interface");
        }
        try {
            XMLLoggingProxyInvocationHandler handler = new XMLLoggingProxyInvocationHandler(implementation, writer);
            return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{interfaceClass}, handler);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
