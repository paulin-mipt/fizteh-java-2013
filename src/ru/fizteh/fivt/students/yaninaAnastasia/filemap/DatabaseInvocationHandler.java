package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DatabaseInvocationHandler implements InvocationHandler {
    public final Writer writer;
    public final Object innerObject;

    public DatabaseInvocationHandler(Writer writer, Object object) {
        this.writer = writer;
        this.innerObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Throwable targetException = null;
        try {
            result = method.invoke(innerObject, args);
        } catch (InvocationTargetException e) {
            targetException = e.getTargetException();
            throw targetException;
        } finally {
            try {
                if (!method.getDeclaringClass().equals(Object.class)) {
                    JSONWriter logWriter = new JSONWriter();
                    logWriter.logTimestamp();
                    logWriter.logClass(innerObject.getClass());
                    logWriter.logMethod(method);
                    logWriter.logArguments(args);
                    if (targetException != null) {
                        logWriter.logThrown(targetException);
                    } else if (!method.getReturnType().equals(void.class)) {
                        logWriter.logReturnValue(result);
                    }
                    writer.write(logWriter.getStringRepresentation() + System.lineSeparator());
                }
            } catch (Throwable e) {
                //
            }
        }
        return result;
    }
}
