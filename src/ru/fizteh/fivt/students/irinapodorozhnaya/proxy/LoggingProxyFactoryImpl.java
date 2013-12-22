package ru.fizteh.fivt.students.irinapodorozhnaya.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import java.io.Writer;
import java.lang.reflect.Proxy;

public class LoggingProxyFactoryImpl implements LoggingProxyFactory {

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {

        if (writer == null || interfaceClass == null || implementation == null) {
            throw new IllegalArgumentException("undefined arguments");
        }

        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("the interface class is not an interface");
        }

        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("the object does not implement the interface");
        }

        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new ProxyInvocationHandler(writer, implementation)
        );
    }

}
