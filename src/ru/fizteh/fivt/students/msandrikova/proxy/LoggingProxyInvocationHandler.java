package ru.fizteh.fivt.students.msandrikova.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.stream.XMLStreamException;

public class LoggingProxyInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;
    
    
    public LoggingProxyInvocationHandler(Writer givenWriter, Object givenImplementation) {
        this.writer = givenWriter;
        this.implementation = givenImplementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(this.implementation, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        
        Object res = null;
        Throwable thrown = null;
        XMLBuilder builder = null;
        try {
            builder = new XMLBuilder(implementation.getClass(), method, args);
        } catch (XMLStreamException e) {
            // Do nothing
        }
        try {
            res = method.invoke(this.implementation, args);
            if (!method.getReturnType().equals(void.class)) {
                try {
                    builder.writeResult(res);    
                } catch (XMLStreamException e) {
                    // Do nothing
                }                
            }
        } catch (InvocationTargetException e) {
            thrown = e.getTargetException();
            try {
                builder.writeThrown(thrown);
            } catch (XMLStreamException e1) {
                // Do nothing
            }
            throw thrown;
        } finally {
            builder.endXML();
            writer.write(builder.toString() + "\n");
        }
        return res;
    }

}
