package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class ProxyFactory implements LoggingProxyFactory {
    public ProxyFactory() {
    }

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer is null");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("Implementation is null");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("InterfaceClass is null");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Not an interface");
        }
        if (!interfaceClass.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException("Incorrect interfaceClass");
        }
        InvocationHandler handler = new ProxyInvocationHandler(writer, implementation);
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[] {interfaceClass},
                handler);
    }

}
