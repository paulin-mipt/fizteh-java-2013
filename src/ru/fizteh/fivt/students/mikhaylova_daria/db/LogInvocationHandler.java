package ru.fizteh.fivt.students.mikhaylova_daria.db;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;


public class LogInvocationHandler implements InvocationHandler {

    private Object proxied;
    private Writer writer;

    class ProviderArrayJSON {
        private Object argument;
        private IdentityHashMap<Object, Object> identifyAttended = new IdentityHashMap<>();

        ProviderArrayJSON(Object argument) {
            this.argument = argument;
        }


        private JSONArray recursiveLog(Object arg, JSONArray creatingArray) {
            JSONArray newCreatingArray = new JSONArray();
            if (arg != null) {
                if (Iterable.class.isAssignableFrom(arg.getClass())) {
                    if (identifyAttended.containsKey(arg)) {
                        creatingArray.put("cyclic");
                    } else {
                        identifyAttended.put(arg, arg);
                        for (Object obj: (Iterable) arg) {
                            try {
                                newCreatingArray = recursiveLog(obj, newCreatingArray);
                            } catch (java.lang.ClassCastException e) {
                                newCreatingArray.put(arg.toString());
                            }
                        }
                        identifyAttended.remove(arg);
                        creatingArray.put(newCreatingArray);
                    }
                } else if (arg.getClass().isArray()) {
                    if (identifyAttended.containsKey(arg)) {
                        creatingArray.put("cyclic");
                    } else {
                        identifyAttended.put(arg, arg);
                        identifyAttended.put(arg, arg);
                        for (Object obj: (Object[]) arg) {
                            try {
                                newCreatingArray = recursiveLog(obj, newCreatingArray);
                            } catch (java.lang.ClassCastException e) {
                                newCreatingArray.put(obj.toString());
                            }
                        }
                        identifyAttended.remove(arg);
                        creatingArray.put(newCreatingArray);
                    }
                } else {
                    try {
                        creatingArray.put(arg);
                    } catch (java.lang.ClassCastException e) {
                        creatingArray.put(arg.toString());
                    }
                }
            } else {
                creatingArray.put(JSONObject.NULL);
            }
            return creatingArray;
        }


        JSONArray getJSONArray() {
            JSONArray creatingArray = new JSONArray();
            return  recursiveLog(argument, creatingArray);
        }
    }


    LogInvocationHandler(Object implementation, Writer writer) {
        this.proxied = implementation;
        this.writer = writer;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnedValue = null;
        JSONObject record = new JSONObject();
        if (!method.getDeclaringClass().equals(Object.class)) {
            try {
                record.put("timestamp", System.currentTimeMillis());
                record.put("class", proxied.getClass().getName());
                record.put("method", method.getName());
                if (args == null) {
                    record.put("arguments", new JSONArray());
                } else if (args.length == 0) {
                    record.put("arguments", new JSONArray());
                } else {
                    ProviderArrayJSON creatorJSONArray = new ProviderArrayJSON(args);
                    record.put("arguments", creatorJSONArray.getJSONArray().get(0));
                }
            } catch (Exception e) {

            }
            Throwable thrown = null;
            try {
                returnedValue = method.invoke(proxied, args);
            } catch (InvocationTargetException e) {
                thrown = e.getTargetException();
            } catch (Throwable e) {
                thrown = e;
            }
            try {
                if (thrown != null) {
                    record.put("thrown", thrown.toString());
                    writer.write(record.toString() + System.lineSeparator());
                } else {
                    if (!method.getReturnType().equals(void.class)) {
                        if (returnedValue == null) {
                            record.put("returnValue",  JSONObject.NULL);
                        } else  {
                            record.put("returnValue",  returnedValue);
                        }
                    }
                    writer.write(record.toString() + System.lineSeparator());
                }
            } catch (Exception exc) {

            }
            if (thrown != null) {
                throw thrown;
            }
        } else {
            try {
                returnedValue = method.invoke(proxied, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (Exception e) {
                throw new RuntimeException("invoking error", e);
            }
        }
        return returnedValue;
    }


}
