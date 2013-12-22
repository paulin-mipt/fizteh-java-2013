package ru.fizteh.fivt.students.vlmazlov.proxy;

import ru.fizteh.fivt.students.vlmazlov.utils.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoggingInvocationHandler implements InvocationHandler {

    private final Appendable writer;
    private final Object target;
    private static final String SEPARATOR = System.getProperty("line.separator");

    public LoggingInvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }

    private Object log(Method method, Object[] args) throws Throwable {
        Throwable thrown = null;
        Object returnValue = null;

        Logger logger = new Logger();
        logger.logMethodCall(method, args, target);

        try {
            returnValue = simpleInvoke(method, args);
            if (method.getReturnType() != void.class) {
                logger.logReturnValue(returnValue);
            }
        } catch (Throwable ex) {
            thrown = ex;
            logger.logThrown(thrown);
        }

        writer.append(logger.toString());

        if (thrown == null) {
            return returnValue;
        } else {
            throw thrown;
        }
    }

    private Object simpleInvoke(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!Object.class.equals(method.getDeclaringClass())) {
            return log(method, args);
        }

        return simpleInvoke(method, args);
    }
}
