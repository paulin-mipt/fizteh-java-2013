package ru.fizteh.fivt.students.dubovpavel.storeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TypeNamesMatcher {
    public static final HashMap<String, Class<?>> CLASS_BY_NAME;
    public static final HashMap<Class<?>, String> NAME_BY_CLASS;
    public static final HashMap<Class<?>, HashSet<Class<?>>> CASTABLE_CLASSES;

    private static void generateCorrespondings(String type, String[] correspondings) {
        HashSet<Class<?>> classes = new HashSet<>();
        for (String corresponding : correspondings) {
            classes.add(CLASS_BY_NAME.get(corresponding));
        }
        CASTABLE_CLASSES.put(CLASS_BY_NAME.get(type), classes);
    }

    static {
        CLASS_BY_NAME = new HashMap<>();
        CLASS_BY_NAME.put("int", Integer.class);
        CLASS_BY_NAME.put("long", Long.class);
        CLASS_BY_NAME.put("byte", Byte.class);
        CLASS_BY_NAME.put("boolean", Boolean.class);
        CLASS_BY_NAME.put("float", Float.class);
        CLASS_BY_NAME.put("double", Double.class);
        CLASS_BY_NAME.put("String", String.class);
    }

    static {
        NAME_BY_CLASS = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry : CLASS_BY_NAME.entrySet()) {
            NAME_BY_CLASS.put(entry.getValue(), entry.getKey());
        }
    }

    static {
        CASTABLE_CLASSES = new HashMap<>();
        generateCorrespondings("long", new String[]{"long", "int", "byte"});
        generateCorrespondings("int", new String[]{"int", "byte"});
        generateCorrespondings("byte", new String[]{"int", "byte"});
        generateCorrespondings("boolean", new String[]{"boolean"});
        generateCorrespondings("float", new String[]{"double", "int", "byte", "long", "float"});
        generateCorrespondings("double", new String[]{"double", "int", "byte", "long", "float"});
        generateCorrespondings("String", new String[]{"int", "long", "byte", "boolean", "float", "double", "String"});
    }
}
