package ru.fizteh.fivt.students.asaitgalin.storable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MultiFileTableTypes {
    INTEGER("int", Integer.class) {
        @Override
        public Object parseValue(String s) {
            return Integer.parseInt(s);
        }
    },
    LONG("long", Long.class) {
        @Override
        public Object parseValue(String s) {
            return Long.parseLong(s);
        }
    },
    BYTE("byte", Byte.class) {
        @Override
        public Object parseValue(String s) {
            return Byte.parseByte(s);
        }
    },
    FLOAT("float", Float.class) {
        @Override
        public Object parseValue(String s) {
            return Float.parseFloat(s);
        }
    },
    DOUBLE("double", Double.class) {
        @Override
        public Object parseValue(String s) {
            return Double.parseDouble(s);
        }
    },
    BOOLEAN("boolean", Boolean.class) {
        @Override
        public Object parseValue(String s) {
            return Boolean.parseBoolean(s);
        }
    },
    STRING("String", String.class) {
        @Override
        public Object parseValue(String s) {
            return s;
        }
    };

    private final String name;
    private final Class<?> clazz;

    private MultiFileTableTypes(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    private static final Map<String, MultiFileTableTypes> nameToType;
    private static final Map<Class<?>, MultiFileTableTypes> classToType;

    static {
        Map<String, MultiFileTableTypes> tmpNameToClass = new HashMap<>();
        Map<Class<?>, MultiFileTableTypes> tmpClassToName = new HashMap<>();
        for (MultiFileTableTypes type : values()) {
            tmpNameToClass.put(type.name, type);
            tmpClassToName.put(type.clazz, type);
        }
        nameToType = Collections.unmodifiableMap(tmpNameToClass);
        classToType = Collections.unmodifiableMap(tmpClassToName);
    }

    public static String getNameByClass(Class<?> clazz) {
        MultiFileTableTypes types = classToType.get(clazz);
        if (types == null) {
            throw new IllegalArgumentException("types: unknown type class");
        }
        return types.name;
    }

    public abstract Object parseValue(String s);

    public static Object parseValueWithClass(String s, Class<?> expectedClass) {
        MultiFileTableTypes types = classToType.get(expectedClass);
        if (types == null) {
            throw new IllegalArgumentException("types: unknown type");
        }
        return types.parseValue(s);
    }

    public static Class<?> getClassByName(String name) {
        MultiFileTableTypes types = nameToType.get(name);
        if (types == null) {
            throw new IllegalArgumentException("types: unknown type name");
        }
        return types.clazz;
    }

}
