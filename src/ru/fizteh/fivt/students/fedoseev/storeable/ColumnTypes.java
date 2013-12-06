package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ColumnTypes {
    BOOLEAN("boolean", Boolean.class) {
        @Override
        public Boolean parseValue(String s) {
            return Boolean.parseBoolean(s);
        }
    },
    BYTE("byte", Byte.class) {
        @Override
        public Byte parseValue(String s) {
            return Byte.parseByte(s);
        }
    },
    DOUBLE("double", Double.class) {
        @Override
        public Double parseValue(String s) {
            return Double.parseDouble(s);
        }
    },
    FLOAT("float", Float.class) {
        @Override
        public Float parseValue(String s) {
            return Float.parseFloat(s);
        }
    },
    INTEGER("int", Integer.class) {
        @Override
        public Integer parseValue(String s) {
            return Integer.parseInt(s);
        }
    },
    LONG("long", Long.class) {
        @Override
        public Long parseValue(String s) {
            return Long.parseLong(s);
        }
    },
    STRING("String", String.class) {
        @Override
        public String parseValue(String s) {
            return s;
        }
    };

    private final String typeName;
    private final Class<?> typeClass;
    private static Map<String, ColumnTypes> namesTypesMap = new HashMap<>();
    private static Map<Class<?>, ColumnTypes> classesTypesMap = new HashMap<>();

    public abstract Object parseValue(String s);

    private ColumnTypes(String typeName, Class<?> typeClass) {
        this.typeName = typeName;
        this.typeClass = typeClass;
    }

    static {
        for (ColumnTypes value : values()) {
            namesTypesMap.put(value.typeName, value);
            classesTypesMap.put(value.typeClass, value);
        }
    }

    public static Class<?> nameToType(String name) {
        ColumnTypes types = namesTypesMap.get(name);

        if (types == null) {
            throw new IllegalArgumentException("ERROR: invalid type name");
        }

        return types.typeClass;
    }

    public static String typeToName(Class<?> type) {
        ColumnTypes types = classesTypesMap.get(type);

        if (types == null) {
            throw new IllegalArgumentException("ERROR: invalid type");
        }

        return types.typeName;
    }

    public static Object commonParseValue(String s, Class<?> type) {
        ColumnTypes types = classesTypesMap.get(type);

        if (types == null) {
            throw new IllegalArgumentException("ERROR: invalid type");
        }

        try {
            return types.parseValue(s);
        } catch (NumberFormatException e) {
            throw new ColumnFormatException(e);
        }
    }

    public static List<Class<?>> getTypesList() {
        List<Class<?>> types = new ArrayList<>();

        for (ColumnTypes value : values()) {
            types.add(value.typeClass);
        }

        return types;
    }
}
