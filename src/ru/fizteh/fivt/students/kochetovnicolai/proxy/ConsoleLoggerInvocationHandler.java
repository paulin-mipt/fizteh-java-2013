package ru.fizteh.fivt.students.kochetovnicolai.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

class ConsoleLoggerInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;
    private IOException ioException = null;

    ConsoleLoggerInvocationHandler(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
    }

    private void addObjectInArray(JSONArray jsonArray, Object anArray, IdentityHashMap<Object, Object> addedElements) {
        if (anArray != null && Iterable.class.isAssignableFrom(anArray.getClass())) {
            if (addedElements.containsKey(anArray)) {
                jsonArray.put("cyclic");
            } else {
                jsonArray.put(resolveIterable((Iterable) anArray, addedElements));
            }
        } else if (anArray != null && anArray.getClass().isArray()) {
            if (addedElements.containsKey(anArray)) {
                jsonArray.put("cyclic");
            } else {
                try {
                    jsonArray.put(createJSONArray((Object[]) anArray, addedElements));
                } catch (ClassCastException e) {
                    jsonArray.put(anArray.toString());
                }
            }
        } else {
            jsonArray.put(anArray);
        }
    }

    private JSONArray createJSONArray(Object[] array, IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object anArray : array) {
            addObjectInArray(jsonArray, anArray, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }

    private JSONArray resolveIterable(Iterable array, IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object anArray : array) {
            addObjectInArray(jsonArray, anArray, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }

    private void writeJSONObject(JSONObject object, Object returned) {
        if (returned == null) {
            Object jsonNull = JSONObject.NULL;
            object.put("returnValue", jsonNull);
        } else {
            if (!returned.equals(Void.class)) {
                object.put("returnValue", returned);
            }
        }
        try {
            writer.write(object.toString() + System.lineSeparator());
        } catch (IOException e) {
            ioException = ioException == null ? e : ioException;
        }
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
        boolean failed = false;
        JSONObject log = null;
        try {
            log = new JSONObject();
            log.put("timestamp", System.currentTimeMillis());
            log.put("class", implementation.getClass().getName());
            log.put("method", method.getName());
            if (args == null || args.length == 0) {
                log.put("arguments", new Object[0]);
            } else {
                log.put("arguments", createJSONArray(args, new IdentityHashMap<>()));
            }
        } catch (Throwable e) {
            failed = true;
        }
        Object returnValue;
        Object writingValue = null;
        try {
            returnValue = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            Throwable exception = e.getTargetException();
            if (!failed) {
                try {
                    log.put("thrown", exception.toString());
                    writeJSONObject(log, Void.class);
                } catch (Throwable t) {
                    failed = true;
                }
            }
            throw e.getTargetException();
        }
        if (!failed) {
            try {
                if (returnValue != null) {
                    if (returnValue.getClass().isArray()) {
                        writingValue = createJSONArray((Object[]) returnValue, new IdentityHashMap<>());
                    } else if (Iterable.class.isAssignableFrom(returnValue.getClass())) {
                        writingValue = resolveIterable((Iterable) returnValue, new IdentityHashMap<>());
                    } else {
                        writingValue = returnValue;
                    }
                } else {
                    Class type = method.getReturnType();
                    if (type.getName().equals("void")) {
                        writingValue = Void.class;
                    }
                }
                writeJSONObject(log, writingValue);
            } catch (Throwable e) {
                failed = true;
            }
        }
        return returnValue;
    }
}
