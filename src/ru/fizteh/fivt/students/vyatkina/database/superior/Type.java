package ru.fizteh.fivt.students.vyatkina.database.superior;

import java.util.HashMap;
import java.util.Map;

public enum Type {
    INTEGER("int", Integer.class),
    LONG("long", Long.class),
    BYTE("byte", Byte.class),
    FLOAT("float", Float.class),
    DOUBLE("double", Double.class),
    BOOLEAN("boolean", Boolean.class),
    STRING("String", String.class);

    private String shortName;
    private Class<?> clazz;

    Type(String shortName, Class<?> clazz) {
        this.shortName = shortName;
        this.clazz = clazz;
    }

    public String getShortName() {
        return shortName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static final Map<String, Class<?>> BY_SHORT_NAME;

    static {
        BY_SHORT_NAME = new HashMap<>();
        for (Type type : Type.values()) {
            BY_SHORT_NAME.put(type.getShortName(), type.getClazz());
        }
    }

    public static final Map<Class<?>, String> BY_CLASS;

    static {
        BY_CLASS = new HashMap<>();
        for (Type type : Type.values()) {
            BY_CLASS.put(type.getClazz(), type.getShortName());
        }
    }
}
