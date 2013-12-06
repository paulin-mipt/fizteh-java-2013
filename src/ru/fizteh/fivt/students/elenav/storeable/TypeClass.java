package ru.fizteh.fivt.students.elenav.storeable;

import java.util.HashMap;
import java.util.Map;

public enum TypeClass {
    
    STRING("String", String.class) {
        public String parse(String s) {
            return s;
        }
    },
    
    INTEGER("int", Integer.class) {
        public Integer parse(String s) {
            return Integer.parseInt(s);
        }
    },
    
    LONG("long", Long.class) {
        public Long parse(String s) {
            return Long.parseLong(s);
        }
    },
    
    BYTE("byte", Byte.class) {
        public Byte parse(String s) {
            return Byte.parseByte(s);
        }
    },
    
    FLOAT("float", Float.class) {
        public Float parse(String s) {
            return Float.parseFloat(s);
        }
    },
    
    DOUBLE("double", Double.class) {
        public Double parse(String s) {
            return Double.parseDouble(s);
        }
    },
    
    BOOLEAN("boolean", Boolean.class) {
        public Boolean parse(String s) {
            return Boolean.parseBoolean(s);
        }
    };

    private final String name;
    private final Class<?> type;
    private static Map<String, TypeClass> withName = new HashMap<>();
    private static Map<Class<?>, TypeClass> withClass = new HashMap<>();
    
    private TypeClass(String n, Class<?> t) {
        name = n;
        type = t;
    }
    
    public abstract Object parse(String s);
    
    public static Class<?> getTypeWithName(String name) {
        TypeClass type = withName.get(name);
        if (type == null) {
            throw new IllegalArgumentException("wrong type (" + name + ")");
        }
        return type.type;
    }
    
    public static String getNameWithType(Class<?> c) {
        TypeClass type = withClass.get(c);
        if (type == null) {
            throw new IllegalArgumentException("wrong type (" + c.getSimpleName() + ")");
        }
        return type.name;
    }
    
    public static Object parse(String name, Class<?> c) {
        TypeClass type = withClass.get(c);
        if (type == null) {
            throw new IllegalArgumentException("wrong type (" + name + ")");
        }
        return type.parse(name);
    }
    
    static {
        for (TypeClass type : values()) {
            withName.put(type.name, type);
            withClass.put(type.type, type);
        }
    }
}
