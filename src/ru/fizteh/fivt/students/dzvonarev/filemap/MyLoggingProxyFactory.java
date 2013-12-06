package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class MyLoggingProxyFactory implements LoggingProxyFactory {

    public MyLoggingProxyFactory() {
    }

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("wrong writer");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("wrong implementation");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("wrong interface class");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("input class is not an interface");
        }
        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException(implementation.getClass().toString()
                    + " doesn't implement interface class");
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new MyInvocationHandler(writer, implementation));
    }

}
