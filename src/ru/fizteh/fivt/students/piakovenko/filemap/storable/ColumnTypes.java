package ru.fizteh.fivt.students.piakovenko.filemap.storable;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 08.11.13
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */



import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.util.HashMap;
import java.util.Map;

public enum ColumnTypes {
    INTEGER("int", Integer.class) {
        public Integer parseValue(String string) {
            return Integer.parseInt(string);
        }
    },
    LONG("long", Long.class) {
        public Long parseValue(String string) {
            return Long.parseLong(string);
        }
    },
    BYTE("byte", Byte.class) {
        public Byte parseValue(String string) {
            return Byte.parseByte(string);
        }
    },
    FLOAT("float", Float.class) {
        public Float parseValue(String string) {
            return Float.parseFloat(string);
        }
    },
    DOUBLE("double", Double.class) {
        public Double parseValue(String string) {
            return Double.parseDouble(string);
        }
    },
    BOOLEAN("boolean", Boolean.class) {
        public Boolean parseValue(String string) {
            return Boolean.parseBoolean(string);
        }
    },
    STRING("String", String.class) {
        public String parseValue(String string) {
            return string;
        }
    };

    private final String name;
    private final Class<?> type;
    private static Map<String, ColumnTypes> nameByClass;
    private static Map<Class<?>, ColumnTypes> classByName;

    private ColumnTypes(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    static {
        HashMap<String, ColumnTypes> namePart = new HashMap<String, ColumnTypes>();
        HashMap<Class<?>, ColumnTypes> classPart = new HashMap<Class<?>, ColumnTypes>();
        for (ColumnTypes value : values()) {
            namePart.put(value.name, value);
            classPart.put(value.type, value);
        }
        nameByClass = namePart;
        classByName = classPart;
    }

    public static Class<?> fromNameToType(String name) {
        ColumnTypes typesFormatter = nameByClass.get(name);
        if (typesFormatter == null) {
            throw new IllegalArgumentException("wrong type (wrong type)");
        }
        return typesFormatter.type;
    }

    public static String fromTypeToName(Class<?> type) {
        ColumnTypes typesFormatter = classByName.get(type);
        if (typesFormatter == null) {
            throw new IllegalArgumentException("wrong type (wrong type)");
        }
        return typesFormatter.name;
    }

    public abstract Object parseValue(String string);

    public static Object parsingValue(String string, Class<?> type) {
        ColumnTypes typesFormatter = classByName.get(type);
        if (typesFormatter == null) {
            throw new IllegalArgumentException("wrong type (wrong type)");
        }
        try {
            return typesFormatter.parseValue(string);
        } catch (NumberFormatException e) {
            throw new ColumnFormatException(e);
        }
    }
}
