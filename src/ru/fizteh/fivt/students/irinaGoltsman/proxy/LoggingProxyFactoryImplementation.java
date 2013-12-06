package ru.fizteh.fivt.students.irinaGoltsman.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.lang.reflect.Proxy;
import java.io.Writer;

public class LoggingProxyFactoryImplementation implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null || interfaceClass == null || implementation == null) {
            throw new IllegalArgumentException("null arguments");
        }
        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("wrong arguments: this object is not an instance of class "
                    + interfaceClass.getName());
        }
        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new ProxyInvocationHandler(writer, implementation));
    }
}
