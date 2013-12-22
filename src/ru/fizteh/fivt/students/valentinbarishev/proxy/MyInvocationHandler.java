package ru.fizteh.fivt.students.valentinbarishev.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {

    private Writer writer;
    private Object implementation;

    public MyInvocationHandler(Writer newWriter, Object newImplementation) {
        writer = newWriter;
        implementation = newImplementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(implementation, args);
        }

        Object result = null;
        Throwable exception = null;
        try {
            result = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
            throw exception;
        } finally {
            try {
                MyLogWriter log = new MyLogWriter(implementation, method, args);
                log.setException(exception);
                log.setReturnValue(result);
                writer.write(log.write() + System.lineSeparator());
                writer.flush();
            } catch (Exception e) {
                //silent
            }
        }

        return result;
    }
}
