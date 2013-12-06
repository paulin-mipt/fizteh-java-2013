package ru.fizteh.fivt.students.irinapodorozhnaya.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyInvocationHandler implements InvocationHandler {

    private final Writer writer;
    private final Object object;

    public ProxyInvocationHandler(Writer writer, Object object) {
        this.writer = writer;
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (!method.getDeclaringClass().equals(Object.class)) {
            try (JSONLogWriter logWriter = new JSONLogWriter(writer))  {
                logWriter.writeTimeStamp();
                logWriter.writeClass(object.getClass());
                logWriter.writeMethod(method);
                logWriter.writeArguments(args);
                try {
                    result = method.invoke(object, args);
                    if (method.getReturnType() != void.class) {
                        logWriter.writeReturnValue(result);
                    }
                } catch (InvocationTargetException e) {
                    logWriter.writeThrown(e.getTargetException());
                    throw e.getTargetException();
                }
            }
        } else {
            try {
                result = method.invoke(object, args);
            } catch (InvocationTargetException e) {
               throw e.getTargetException();
            }
        }
        return result;
    }
}
