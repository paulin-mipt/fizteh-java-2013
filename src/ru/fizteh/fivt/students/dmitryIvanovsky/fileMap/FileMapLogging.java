package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileMapLogging implements InvocationHandler {
    Object object;
    Writer writer;

    FileMapLogging(Object implementation, Writer writer) {
        this.object = implementation;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = null;
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                returnValue = method.invoke(object, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            JSONObject record = null;

            try {
                record = new JSONObject();
                try {
                    record.put("timestamp", System.currentTimeMillis());
                } catch (Exception e) {
                    //pass
                }
                record.put("class", object.getClass().getName());
                record.put("method", method.getName());

                if (args == null || args.length == 0) {
                    record.put("arguments", new JSONArray());
                } else {
                    FileMapLoggingJson creatorJSONArray = new FileMapLoggingJson(args);
                    Object ob = creatorJSONArray.getJSONArray().get(0);
                    record.put("arguments", ob);
                }
            } catch (Exception e) {
                //pass
            }

            try {
                returnValue = method.invoke(object, args);
            } catch (InvocationTargetException e) {
                try {
                    if (record == null) {
                        record = new JSONObject();
                    }
                    record.put("thrown", e.getTargetException().toString());
                    writer.write(record.toString() + System.lineSeparator());
                } catch (Exception err) {
                    //pass
                }
                throw e.getTargetException();
            }

            try {
                if (record == null) {
                    record = new JSONObject();
                }
                if (!method.getReturnType().equals(void.class)) {
                    if (returnValue == null) {
                        record.put("returnValue", JSONObject.NULL);
                    } else {
                        JSONObject copy = new JSONObject(record, JSONObject.getNames(record));
                        record.put("returnValue", returnValue);
                        if (record.toString() == null) {
                            record = copy;
                            record.put("returnValue", returnValue.toString());
                        }
                    }
                }
                writer.write(record.toString() + System.lineSeparator());
            } catch (Exception e) {
                //pass
            }

        }
        return returnValue;
    }
}
