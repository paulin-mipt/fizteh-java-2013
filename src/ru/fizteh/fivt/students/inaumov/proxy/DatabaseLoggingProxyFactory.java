package ru.fizteh.fivt.students.inaumov.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.inaumov.proxy.handlers.DatabaseProxyHandler;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class DatabaseLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("writer can't be null");
        }

        if (interfaceClass == null) {
            throw new IllegalArgumentException("interface class can't be null");
        }

        if (implementation == null) {
            throw new IllegalArgumentException("implementation can't be null");
        }

        if (!interfaceClass.isInstance(implementation) || !interfaceClass.isInterface()) {
            throw new IllegalArgumentException("wrong interface class");
        }

        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new DatabaseProxyHandler(writer, implementation));
    }
}
