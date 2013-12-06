package ru.fizteh.fivt.students.adanilyak.proxy;

import ru.fizteh.fivt.students.adanilyak.logformater.XMLformatter;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Alexander
 * Date: 23.11.13
 * Time: 11:13
 */
public class ProxyInvocationHandler implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;
    private final Lock lock = new ReentrantLock(true);

    public ProxyInvocationHandler(Writer givenWriter, Object object) {
        writer = givenWriter;
        implementation = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!CheckOnCorrect.goodMethodForProxy(method)) {
            return method.invoke(implementation, args);
        }
        Object result = null;
        lock.lock();
        try {
            XMLformatter formatter = new XMLformatter();

            formatter.writeTimeStamp();
            formatter.writeClass(implementation.getClass());
            formatter.writeMethod(method);
            formatter.writeArguments(args);
            try {
                result = method.invoke(implementation, args);
                if (!method.getReturnType().equals(void.class)) {
                    formatter.writeReturnValue(result);
                }
            } catch (InvocationTargetException exc) {
                Throwable targetException = exc.getTargetException();
                formatter.writeThrown(targetException);
                throw targetException;
            } catch (Exception exc) {
                // Something went wrong
            } finally {
                try {
                    if (method.getDeclaringClass() != Object.class) {
                        formatter.close();
                        writer.write(formatter.toString() + "\n");
                    }
                } catch (IOException ignored) {
                    // Ignore exceptions
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }
}
