package ru.fizteh.fivt.students.inaumov.proxy.handlers;

import ru.fizteh.fivt.students.inaumov.proxy.XMLFormatter;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DatabaseProxyHandler implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;

    public DatabaseProxyHandler(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Throwable throwable = null;

        try {
            result = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            throwable = e.getTargetException();
        }

        if (method.getDeclaringClass() != Object.class) {
            try {
                XMLFormatter formatter = new XMLFormatter();
                formatter.writeMethodLog(method, args, implementation, throwable, result);
                writer.write(formatter.toString() + "\n");
            } catch (Throwable e) {
                if (throwable != null) {
                    throwable.addSuppressed(e);
                }
            }
        }

        if (throwable != null) {
            throw throwable;
        }

        return result;
    }
}
