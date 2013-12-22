package ru.fizteh.fivt.students.anastasyev.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class MyLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer is null");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("Implementation is null");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class is null");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Interface class expected");
        }
        if (!interfaceClass.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException("Incorrect interface class");
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new MyInvocationHandler(writer, implementation));
    }
}
