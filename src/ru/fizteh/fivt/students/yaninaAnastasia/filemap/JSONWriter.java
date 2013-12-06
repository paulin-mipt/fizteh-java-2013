package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class JSONWriter {
    private JSONObject jsonLog = new JSONObject();
    private final IdentityHashMap<Object, Boolean> objects = new IdentityHashMap<Object, Boolean>();

    public void logTimestamp() {
        jsonLog = jsonLog.put("timestamp", System.currentTimeMillis());
    }

    public void logClass(Class<?> clazz) {
        jsonLog = jsonLog.put("class", clazz.getName());
    }

    public void logMethod(Method method) {
        jsonLog = jsonLog.put("method", method.getName());
    }

    public void logArguments(Object[] args) {
        if (args == null) {
            jsonLog = jsonLog.put("arguments", new JSONArray());
            return;
        }
        jsonLog = jsonLog.put("arguments", makeJSONArray(Arrays.asList(args)));
        objects.clear();
    }

    public void logReturnValue(Object result) {
        Object toWrite = null;
        if (result != null) {
            if (result instanceof Iterable) {
                toWrite = makeJSONArray((Iterable) result);
            } else {
                toWrite = result;
            }
        } else {
            toWrite = JSONObject.NULL;
        }
        jsonLog = jsonLog.put("returnValue", toWrite);
    }

    public void logThrown(Throwable cause) {
        jsonLog = jsonLog.put("thrown", cause.toString());
    }

    public String getStringRepresentation() {
        return jsonLog.toString(2);
    }

    private JSONArray makeJSONArray(Iterable collection) {
        JSONArray result = new JSONArray();
        for (Object value : collection) {
            if (value == null) {
                result.put(value);
                continue;
            }

            if (value.getClass().isArray()) {
                result.put(value.toString());
                continue;
            }

            boolean isContainer = false;
            boolean isEmpty = false;

            if (value instanceof Iterable) {
                isContainer = true;
                isEmpty = !((Iterable) value).iterator().hasNext();
            }

            if (objects.containsKey(value) && isContainer && !isEmpty) {

                result.put("cyclic");
                continue;
            }

            objects.put(value, true);

            if (isContainer) {
                result.put(makeJSONArray((Iterable) value));
                continue;
            }

            result.put(value);
        }
        return result;
    }
}
