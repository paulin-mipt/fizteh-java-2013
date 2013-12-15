package ru.fizteh.fivt.students.dubovpavel.storeable;

import java.util.HashMap;
import java.util.Map;

public class TypesCaster {
    // Supports only task's types
    public static final HashMap<String, Class<?>> SUPPORTED_NAMES;
    public static final HashMap<Class<?>, String> SUPPORTED_TYPES;
    static {
        SUPPORTED_NAMES = new HashMap<>();
        SUPPORTED_NAMES.put("int", Integer.class);
        SUPPORTED_NAMES.put("long", Long.class);
        SUPPORTED_NAMES.put("byte", Byte.class);
        SUPPORTED_NAMES.put("float", Float.class);
        SUPPORTED_NAMES.put("double", Double.class);
        SUPPORTED_NAMES.put("boolean", Boolean.class);
        SUPPORTED_NAMES.put("String", String.class);

        SUPPORTED_TYPES = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry: SUPPORTED_NAMES.entrySet()) {
            SUPPORTED_TYPES.put(entry.getValue(), entry.getKey());
        }
    }
    public static class TypesCasterException extends Exception {
        TypesCasterException(String msg) {
            super(msg);
        }
    }

    public static <T> T cast(Object value, Class<?> type) throws TypesCasterException {
        if (value == null) {
            return null;
        }
        if (type.equals(String.class)) {
            return (T) value.toString();
        } else {
            try {
                return (T) type.getMethod("valueOf", new Class[]{String.class}).invoke(
                        null, value.toString());
            } catch (Exception e) {
                throw new TypesCasterException(
                        String.format("Object can not be casted to the type %s", type.getName()));
            }
        }
    }
}
