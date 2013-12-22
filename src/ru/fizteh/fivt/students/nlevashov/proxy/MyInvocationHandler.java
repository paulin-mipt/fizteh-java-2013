package ru.fizteh.fivt.students.nlevashov.proxy;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;

public class MyInvocationHandler implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;

    public MyInvocationHandler(Writer myWriter, Object myImplementation) {
        writer = myWriter;
        implementation = myImplementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(implementation, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        Object result = null;
        Throwable exception = null;
        try {
            result = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
        }

        try {
            JSONObject log = new JSONObject();
            log.put("timestamp", System.currentTimeMillis());
            log.put("class", implementation.getClass().getName());
            log.put("method", method.getName());
            log.put("arguments", getJSONArrayFromArray(args, new IdentityHashMap<>()));
            if (exception == null) {
                if (!method.getReturnType().equals(void.class)) {
                    if (result == null) {
                        log.put("returnValue", JSONObject.NULL);
                    } else if (result.getClass().isArray()) {
                        try {
                            log.put("returnValue", getJSONArrayFromArray((Object[]) result, new IdentityHashMap<>()));
                        } catch (ClassCastException e) {
                            log.put("returnValue", result.toString());
                        }
                    } else if (Iterable.class.isAssignableFrom(result.getClass())) {
                        try {
                            log.put("returnValue",
                                    getJSONArrayFromIterable((Iterable<?>) result, new IdentityHashMap<>()));
                        } catch (ClassCastException e) {
                            log.put("returnValue", result.toString());
                        }
                    } else {
                        log.put("returnValue", result);
                    }
                }
            } else {
                log.put("thrown", exception.toString());
            }
            writer.write(log.toString() + System.lineSeparator());
        } catch (Throwable exc) {
            // Немного странно, что try без catch или finally это неправильно, в то же время как и пустой catch или
            // finally. Но комментарий, который никак не влияет на исполнение кода, все изменят к лучшему. Java тащит.
        }

        if (exception != null) {
            throw exception;
        }
        return result;
    }

    private JSONArray getJSONArrayFromArray(Object[] args, IdentityHashMap<Object, Object> map) {
        JSONArray a = new JSONArray();
        if (args != null) {
            map.put(args, null);
            for (Object arg : args) {
                if (arg == null) {
                    a.put(JSONObject.NULL);
                } else if (map.containsKey(arg)) {
                    a.put("cyclic");
                } else if (arg.getClass().isArray()) {
                    try {
                        a.put(getJSONArrayFromArray((Object[]) arg, map));
                    } catch (ClassCastException e) {
                        a.put(arg.toString());
                    }
                } else if (Iterable.class.isAssignableFrom(arg.getClass())) {
                    try {
                        a.put(getJSONArrayFromIterable((Iterable<?>) arg, map));
                    } catch (ClassCastException e) {
                        a.put(arg.toString());
                    }
                } else {
                    a.put(arg);
                }
            }
            map.remove(args);
        }
        return a;
    }

    private JSONArray getJSONArrayFromIterable(Iterable<?> args, IdentityHashMap<Object, Object> map) {
        JSONArray a = new JSONArray();
        if (args != null) {
            map.put(args, null);
            for (Object arg : args) {
                if (arg == null) {
                    a.put(JSONObject.NULL);
                } else if (map.containsKey(arg)) {
                    a.put("cyclic");
                } else if (arg.getClass().isArray()) {
                    try {
                        a.put(getJSONArrayFromArray((Object[]) arg, map));
                    } catch (ClassCastException e) {
                        a.put(arg.toString());
                    }
                } else if (Iterable.class.isAssignableFrom(arg.getClass())) {
                    try {
                        a.put(getJSONArrayFromIterable((Iterable<?>) arg, map));
                    } catch (ClassCastException e) {
                        a.put(arg.toString());
                    }
                } else {
                    a.put(arg);
                }
            }
            map.remove(args);
        }
        return a;
    }
}
