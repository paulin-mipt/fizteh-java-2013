package ru.fizteh.fivt.students.nadezhdakaratsapova.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class LoggingInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;

    public LoggingInvocationHandler(Writer writer, Object impl) {
        this.writer = writer;
        implementation = impl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                result = method.invoke(implementation, args);
                return result;
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            JSONObject jsonLog = new JSONObject();
            jsonLog.put("timestamp", System.currentTimeMillis());
            jsonLog.put("class", implementation.getClass().getName());
            jsonLog.put("method", method.getName());
            JSONArray array = new JSONArray();
            if (args != null) {
                writeArgument(array, Arrays.asList(args),
                        Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>()));
            }
            jsonLog.put("arguments", array);
            try {
                result = method.invoke(implementation, args);
                if (!method.getReturnType().isAssignableFrom(void.class)) {
                    JSONArray jsonArray = new JSONArray();
                    if (result != null) {
                        if (result instanceof Iterable) {
                            writeArgument(jsonArray, (Iterable) result,
                                    Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>()));
                            jsonLog.put("returnValue", jsonArray);
                        } else {
                            if (result.getClass().isArray()) {
                                writeArgument(jsonArray, Arrays.asList((Object[]) result),
                                        Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>()));
                                jsonLog.put("returnValue", jsonArray);
                            } else {
                                jsonLog.put("returnValue", result);
                            }
                        }
                    } else {
                        jsonLog.put("returnValue", JSONObject.NULL);
                    }

                }


            } catch (InvocationTargetException e) {
                Throwable thrown = e.getTargetException();
                jsonLog.put("thrown", thrown.toString());
                throw thrown;
            } finally {
                try {
                    if (!method.getDeclaringClass().equals(Object.class)) {
                        writer.write(jsonLog.toString(2));
                        writer.write(System.lineSeparator());
                    }
                } catch (IOException e) {
                    //do nothing
                }

            }
        }
        return result;
    }

    public void writeArgument(JSONArray cmdArgs, Iterable args, Set<Object> prevArgs) {
        prevArgs.add(args);
        for (Object arg : args) {
            if (arg == null) {
                cmdArgs.put(arg);
            } else {
                if (arg instanceof Iterable) {
                    if (prevArgs.contains(arg)) {
                        cmdArgs.put("cyclic");
                    } else {
                        JSONArray array = new JSONArray();
                        writeArgument(array, (Iterable) arg, prevArgs);
                        cmdArgs.put(array);
                    }
                } else {
                    if (arg.getClass().isArray()) {
                        cmdArgs.put(arg.toString());
                    } else {
                        cmdArgs.put(arg);
                    }
                }
            }
        }
    }
}
