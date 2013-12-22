package ru.fizteh.fivt.students.vlmazlov.utils;

import java.util.HashMap;
import java.util.Map;

public enum TypeName {
    INTEGER(Integer.class, "int"),
    LONG(Long.class, "long"),
    BOOLEAN(Boolean.class, "boolean"),
    DOUBLE(Double.class, "double"),
    FLOAT(Float.class, "float"),
    STRING(String.class, "String"),
    BYTE(Byte.class, "byte");

    private Class<?> clazz;
    private String type;
    private static Map<Class<?>, String> typeByClass;
    private static Map<String, Class<?>> classByType;
    private static Map<Class<?>, TypeName> byClass;


    private TypeName(Class<?> clazz, String type) {
        this.clazz = clazz;
        this.type = type;
    }

    static {
        typeByClass = new HashMap<Class<?>, String>();
        classByType = new HashMap<String, Class<?>>();
        byClass = new HashMap<Class<?>, TypeName>();

        for (TypeName typeName : TypeName.values()) {
            typeByClass.put(typeName.clazz, typeName.type);
            classByType.put(typeName.type, typeName.clazz);
            byClass.put(typeName.clazz, typeName);
        }
    }

    public static String getNameByClass(Class<?> clazz) {
        return typeByClass.get(clazz);
    }

    public static Class<?> getClassByName(String type) {
        return classByType.get(type);
    }

    public static TypeName getByClass(Class<?> clazz) {
        return byClass.get(clazz);
    }
}
