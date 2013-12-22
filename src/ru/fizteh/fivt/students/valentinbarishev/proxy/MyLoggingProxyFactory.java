package ru.fizteh.fivt.students.valentinbarishev.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import java.io.Writer;
import java.lang.reflect.Proxy;

public class MyLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer shouldn't be null!");
        }

        if (implementation == null) {
            throw new IllegalArgumentException("Implementation shouldn't be null!");
        }

        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class shouldn't be null!");
        }

        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.toString() + " is not an interface!");
        }

        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException(implementation.getClass().toString() + " isn't implement "
                    + interfaceClass.toString());
        }

        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new MyInvocationHandler(writer, implementation));
    }
}
