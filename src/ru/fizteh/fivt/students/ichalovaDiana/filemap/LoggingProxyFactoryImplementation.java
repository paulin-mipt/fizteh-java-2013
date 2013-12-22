package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.Writer;
import java.lang.reflect.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class LoggingProxyFactoryImplementation implements LoggingProxyFactory {

    public LoggingProxyFactoryImplementation() {};
    
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null || implementation == null || interfaceClass == null) {
            throw new IllegalArgumentException("null arguments");
        }
        
        if (!interfaceClass.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException("implementation doesn't implement iterfaceClass");
        }
        
        return Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{interfaceClass},
                new LoggingJSONInvocationHandler(implementation, writer));
    }

}
