package ru.fizteh.fivt.students.vyatkina.database.logging;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class LoggingProxyFactoryImp implements LoggingProxyFactory {

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null || implementation == null || interfaceClass == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass + " is not an interface");
        }
        if (!interfaceClass.isAssignableFrom(implementation.getClass())) {
           throw new IllegalArgumentException("Implementation doesn't implement interface");
        }

        XMLStreamWriter xmlStreamWriter = null;
        try {
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        }
        catch (XMLStreamException e) {
           throw new WrappedIOException(e);
        }

        InvocationHandler invocationHandler = new XMLLoggerInvocationHandler(xmlStreamWriter,implementation);
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[] {interfaceClass}, invocationHandler);

    }
}
