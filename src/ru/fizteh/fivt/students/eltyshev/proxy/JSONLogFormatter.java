package ru.fizteh.fivt.students.eltyshev.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class JSONLogFormatter {
    private JSONObject jsonObject = new JSONObject();
    private final IdentityHashMap<Object, Boolean> objects = new IdentityHashMap<Object, Boolean>();

    public void writeTimestamp() {
        jsonObject = jsonObject.put(JSONAttributeNames.TIMESTAMP.name, System.currentTimeMillis());
    }

    public void writeClass(Class<?> clazz) {
        jsonObject = jsonObject.put(JSONAttributeNames.CLASS.name, clazz.getName());
    }

    public void writeMethod(Method method) {
        jsonObject = jsonObject.put(JSONAttributeNames.METHOD.name, method.getName());
    }

    public void writeArguments(Object[] args) {
        if (args == null) {
            jsonObject = jsonObject.put(JSONAttributeNames.ARGUMENTS.name, new JSONArray());
            return;
        }
        jsonObject = jsonObject.put(JSONAttributeNames.ARGUMENTS.name, makeJSONArray(Arrays.asList(args)));
        objects.clear();
    }

    public void writeReturnValue(Object result) {
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
        jsonObject = jsonObject.put(JSONAttributeNames.RETURN_VALUE.name, toWrite);
    }

    public void writeThrown(Throwable cause) {
        jsonObject = jsonObject.put(JSONAttributeNames.THROWN.name, cause.toString());
    }

    public String getStringRepresentation() {
        return jsonObject.toString(2);
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
                isEmpty = ((Iterable) value).iterator().hasNext() == false;
            }

            if (objects.containsKey(value) && isContainer && !isEmpty) {

                result.put(JSONAttributeNames.CYCLIC.name);
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

enum JSONAttributeNames {
    TIMESTAMP("timestamp"),
    CLASS("class"),
    METHOD("method"),
    ARGUMENTS("arguments"),
    RETURN_VALUE("returnValue"),
    THROWN("thrown"),
    CYCLIC("cyclic");

    public String name;

    JSONAttributeNames(String name) {
        this.name = name;
    }
}
