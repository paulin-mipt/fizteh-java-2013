package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class DatabaseLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object object, Class<?> inputClass) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer is null");
        }
        if (inputClass == null) {
            throw new IllegalArgumentException("Required class is null");
        }
        if (!inputClass.isInstance(object)) {
            throw new IllegalArgumentException("There is no implementing from the required class");
        }
        if (!inputClass.isInterface()) {
            throw new IllegalArgumentException("Required class is not an interface");
        }
        return Proxy.newProxyInstance(
                object.getClass().getClassLoader(),
                new Class[]{inputClass},
                new DatabaseInvocationHandler(writer, object));
    }
}
