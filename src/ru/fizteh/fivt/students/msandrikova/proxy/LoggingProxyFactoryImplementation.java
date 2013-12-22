package ru.fizteh.fivt.students.msandrikova.proxy;

import java.io.Writer;
import java.lang.reflect.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class LoggingProxyFactoryImplementation implements LoggingProxyFactory {

    @Override
    public Object wrap(Writer writer, Object implementation,
            Class<?> interfaceClass) {
        if (writer == null || interfaceClass == null || !interfaceClass.isInterface() 
                || !interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("bad proxy arguments");
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass}, 
                new LoggingProxyInvocationHandler(writer, implementation));
    }

}
