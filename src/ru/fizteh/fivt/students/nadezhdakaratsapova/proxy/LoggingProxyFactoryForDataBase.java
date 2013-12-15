package ru.fizteh.fivt.students.nadezhdakaratsapova.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class LoggingProxyFactoryForDataBase implements LoggingProxyFactory {

    public LoggingProxyFactoryForDataBase() {

    }

    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Logging wrap: the null writer is not allowed");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("Logging wrap: the null implementation is not allowed");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Logging wrap: the null interfaceClass is not allowed");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Logging wrap: interface is not an interface");
        }
        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("interfaceClass and implementation aren't connected");
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new LoggingInvocationHandler(writer, implementation));
    }
}
