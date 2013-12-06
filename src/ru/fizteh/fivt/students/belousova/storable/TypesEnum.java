package ru.fizteh.fivt.students.belousova.storable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TypesEnum {
    INTEGER("int", Integer.class),
    LONG("long", Long.class),
    BYTE("byte", Byte.class),
    FLOAT("float", Float.class),
    DOUBLE("double", Double.class),
    BOOLEAN("boolean", Boolean.class),
    STRING("String", String.class);


    private final String signature;
    private final Class<?> clazz;

    private TypesEnum(String signature, Class<?> clazz) {
        this.signature = signature;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getSignature() {
        return signature;
    }

    private static final Map<String, TypesEnum> BY_NAME;

    static {
        Map<String, TypesEnum> map = new HashMap<>();
        for (TypesEnum type : values()) {
            map.put(type.getSignature(), type);
        }
        BY_NAME = Collections.unmodifiableMap(map);
    }

    public static TypesEnum getBySignature(String signature) {
        return BY_NAME.get(signature);
    }

    private static final Map<Class<?>, TypesEnum> BY_CLAZZ;

    static {
        Map<Class<?>, TypesEnum> map = new HashMap<>();
        for (TypesEnum type : values()) {
            map.put(type.getClazz(), type);
        }
        BY_CLAZZ = Collections.unmodifiableMap(map);
    }

    public static TypesEnum getByClass(Class<?> clazz) {
        return BY_CLAZZ.get(clazz);
    }

}
