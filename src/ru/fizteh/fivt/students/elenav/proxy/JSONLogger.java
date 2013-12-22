package ru.fizteh.fivt.students.elenav.proxy;

import java.io.Writer;
import java.lang.reflect.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class JSONLogger implements LoggingProxyFactory {
    
    @Override
    public Object wrap(Writer writer, Object im, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("writer is null");
        }
        if (im == null) {
            throw new IllegalArgumentException("implementation is null");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("interfaceClass is null");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("interfaceClass is not an interface");
        }
        if (!interfaceClass.isInstance(im)) {
            throw new IllegalArgumentException("interfaceClass is not implemented with object");
        }
        
        return Proxy.newProxyInstance(im.getClass().getClassLoader(), new Class[] {interfaceClass}, 
                new JSONHandler(writer, im));
    }

        
    
}
