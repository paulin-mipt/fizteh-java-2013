package ru.fizteh.fivt.students.chernigovsky.storeable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TypeEnum {
    INTEGER("int", Integer.class),
    LONG("long", Long.class),
    BYTE("byte", Byte.class),
    FLOAT("float", Float.class),
    DOUBLE("double", Double.class),
    BOOLEAN("boolean", Boolean.class),
    STRING("String", String.class);


    private final String signature;
    private final Class<?> clazz;

    private TypeEnum(String signature, Class<?> clazz) {
        this.signature = signature;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getSignature() {
        return signature;
    }

    private static final Map<String, TypeEnum> BY_NAME;
    static {
        Map<String, TypeEnum> map = new HashMap<>();
        for (TypeEnum type : values()) {
            map.put(type.getSignature(), type);
        }
        BY_NAME = Collections.unmodifiableMap(map);
    }

    public static TypeEnum getBySignature(String signature) {
        return BY_NAME.get(signature);
    }

    private static final Map<Class<?>, TypeEnum> BY_CLAZZ;
    static {
        Map<Class<?>, TypeEnum> map = new HashMap<>();
        for (TypeEnum type : values()) {
            map.put(type.getClazz(), type);
        }
        BY_CLAZZ = Collections.unmodifiableMap(map);
    }

    public static TypeEnum getByClass(Class<?> clazz) {
        return BY_CLAZZ.get(clazz);
    }

}