package ru.fizteh.fivt.students.adanilyak.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * User: Alexander
 * Date: 22.11.13
 * Time: 1:44
 */
public class ProxyLoggingFactoryImplementation implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer,
                       Object implementation,
                       Class<?> interfaceClass) {

        if (!CheckOnCorrect.goodProxyArguments(writer, implementation, interfaceClass)) {
            throw new IllegalArgumentException("proxy logging factory implementation: bad arguments");
        }
        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new ProxyInvocationHandler(writer, implementation));
    }
}
