package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoggingJSONInvocationHandler implements InvocationHandler {

    private final Object target;
    private final Writer writer;
    
    LoggingJSONInvocationHandler(Object target, Writer writer) {
        this.target = target;
        this.writer = writer;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        Object result = null;
        
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                result = method.invoke(target, args);
                return result;
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                throw targetException;
            }
        }
        
        JSONObject log;
        try {
            log = new JSONObject();
            log.put("timestamp", System.currentTimeMillis());
            log.put("class", target.getClass().getName());
            log.put("method", method.getName());
            
            JSONArray arguments = new JSONArray();
       
            if (args != null) {
                for (int i = 0; i < args.length; ++i) {
                    logArgument(args[i], arguments, new IdentityHashMap<Object, Object>());
                }
            }
            log.put("arguments", arguments);
        } catch (Exception e) {
            try {
                result = method.invoke(target, args);
                return result;
            } catch (InvocationTargetException e1) {
                Throwable targetException = e1.getTargetException();
                throw targetException;
            }
        }
        
        try {
            result = method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            try {
                log.put("thrown", targetException.toString());
                writer.write(log.toString() + System.lineSeparator());
            } catch (Exception e1) {
            }
            throw targetException;
        } catch (Exception e) {
        }
        
        try {
            if (method.getReturnType() != void.class) {
                log.put("returnValue", (result != null) ? result : JSONObject.NULL);
            }
        } catch (Exception e) {
        }
        
        try {
            writer.write(log.toString() + System.lineSeparator());
        } catch (Exception e) {
        }
        
        return result;
    }
    
    private void logArgument(Object argument, JSONArray arguments, Map<Object, Object> cycles) {
        if (argument == null) {
            arguments.put(JSONObject.NULL);
        } else if (cycles.containsKey(argument)) {
            arguments.put("cyclic");
        } else if (Iterable.class.isAssignableFrom(argument.getClass())) {
            JSONArray list = new JSONArray();
            
            cycles.put(argument, null);
            for (Object element : (Iterable<Object>) argument) {
                logArgument(element, list, cycles);
            }
            cycles.remove(argument);
            
            arguments.put(list);
        } else if (argument.getClass().isArray()) {
            arguments.put(argument.toString());
        } else {
            arguments.put(argument);
        }
    }

}
