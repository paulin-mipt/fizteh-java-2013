package ru.fizteh.fivt.students.eltyshev.proxy;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyInvocationHandler implements InvocationHandler {

    private final Writer writer;
    private final Object implementation;

    public ProxyInvocationHandler(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        JSONLogFormatter formatter = new JSONLogFormatter();
        formatter.writeTimestamp();
        formatter.writeClass(implementation.getClass());
        formatter.writeMethod(method);
        formatter.writeArguments(args);

        try {
            result = method.invoke(implementation, args);
            if (!method.getReturnType().getName().equals("void")) {
                formatter.writeReturnValue(result);
            }
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            formatter.writeThrown(targetException);
            throw targetException;
        } catch (Exception e) {
            // cant do anything...
        } finally {
            try {
                if (!method.getDeclaringClass().equals(Object.class)) {
                    writer.write(formatter.getStringRepresentation() + "\n");
                }
            } catch (IOException e) {
                // I should be silent
            }
        }
        return result;
    }
}
