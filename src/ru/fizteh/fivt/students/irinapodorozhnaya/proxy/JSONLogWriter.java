package ru.fizteh.fivt.students.irinapodorozhnaya.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class JSONLogWriter implements AutoCloseable {

    private final Writer writer;
    private final ThreadLocal<JSONObject> jsonObject = new ThreadLocal<JSONObject>() {
        public JSONObject initialValue() {
            return new JSONObject();
        }
    };
    private final ThreadLocal<Set<Object>> containedArguments = new ThreadLocal<Set<Object>>() {
        public Set<Object> initialValue() {
            return Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
        }
    };

    public JSONLogWriter(Writer writer) {
        this.writer = writer;
    }

    public void writeTimeStamp() {
        jsonObject.get().put("timestamp", System.currentTimeMillis());

    }

    public void writeClass(Class<?> classObject) {
        jsonObject.get().put("class", classObject.getName());
    }

    public void writeThrown(Throwable throwable) {
        jsonObject.get().put("thrown", throwable.toString());
    }

    public void writeMethod(Method method) {
        jsonObject.get().put("method", method.getName());
    }

    public void writeReturnValue(Object value) {
        JSONArray jsonArray = new JSONArray();
        if (value != null) {
            if (value instanceof Iterable) {
                writeIterable((Iterable) value, jsonArray);
            } else if (value.getClass().isArray()) {
                writeIterable(Arrays.asList((Object[]) value), jsonArray);
            } else {
                jsonObject.get().put("returnValue", value);
                return;
            }
        } else {
            jsonObject.get().put("returnValue", JSONObject.NULL);
            return;
        }
        jsonObject.get().put("returnValue", jsonArray);
    }

    public void writeArguments(Object[] arguments) {
        ThreadLocal<JSONArray> jsonArray = new ThreadLocal<JSONArray>() {
            public JSONArray initialValue() {
                return new JSONArray();
            }
        };
        if (arguments != null) {
            writeIterable(Arrays.asList(arguments), jsonArray.get());
        }
        jsonObject.get().put("arguments", jsonArray.get());
    }

    private void writeIterable(Iterable args, JSONArray jsonArray) {
        containedArguments.get().add(args);
        for (Object argument: args) {
            if (argument == null) {
                jsonArray.put(argument);
            } else if (argument instanceof Iterable) {
                if (containedArguments.get().contains(argument)) {
                    jsonArray.put("cyclic");
                } else {
                    JSONArray array = new JSONArray();
                    writeIterable((Iterable) argument, array);
                    jsonArray.put(array);
                }
            } else if (argument.getClass().isArray()) {
                jsonArray.put(argument.toString());
            } else {
                jsonArray.put(argument);
            }
        }
    }

    @Override
    public void close() {
        try {
            synchronized (writer) {
                writer.write(jsonObject.get().toString(2) + '\n');
            }
        } catch (IOException e) {
            //do nothing
        }
    }
}
