package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.Writer;
import java.lang.reflect.Proxy;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class MyLoggingProxyFactory implements LoggingProxyFactory {

    public MyLoggingProxyFactory() {
    }
    
    
    @Override
    public Object wrap(Writer writer, Object implementation,
            Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("writer not set");
        }

        if (implementation == null) {
            throw new IllegalArgumentException("implementation not set");
        }

        if (interfaceClass == null) {
            throw new IllegalArgumentException("interface not set");
        }

        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.toString() + " is not an interface");
        }

        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException(implementation.getClass().toString() + " doesn't implement "
                    + interfaceClass.toString());
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new MyInvocationHandler(writer, implementation));
    }
}
