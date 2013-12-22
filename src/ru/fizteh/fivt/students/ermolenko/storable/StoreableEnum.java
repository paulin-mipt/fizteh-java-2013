package ru.fizteh.fivt.students.ermolenko.storable;

import java.util.*;

public enum StoreableEnum {

    INTEGER("int", Integer.class) {
        public Object parseValue(String s) {
            return Integer.parseInt(s);
        }
    },
    LONG("long", Long.class) {
        public Object parseValue(String s) {
            return Long.parseLong(s);
        }
    },
    DOUBLE("double", Double.class) {
        public Object parseValue(String s) {
            return Double.parseDouble(s);
        }
    },
    FLOAT("float", Float.class) {
        public Object parseValue(String s) {
            return Float.parseFloat(s);
        }
    },
    BYTE("byte", Byte.class) {
        public Object parseValue(String s) {
            return Byte.parseByte(s);
        }
    },
    BOOLEAN("boolean", Boolean.class) {
        public Object parseValue(String s) {
            return Boolean.parseBoolean(s);
        }
    },
    STRING("String", String.class) {
        public Object parseValue(String s) {
            return s;
        }
    };

    public abstract Object parseValue(String value);

    private String nameOfClass;
    private Class<?> theClass;

    private StoreableEnum(String name, Class<?> inClass) {

        this.nameOfClass = name;
        this.theClass = inClass;
    }

    private static Map<String, StoreableEnum> dataBaseNamesToType;
    private static Map<Class<?>, StoreableEnum> dataBaseClassesToType;
    public static Set<String> dataBaseCorrectNamesOfClasses;

    static {

        Map<String, StoreableEnum> dataBaseNamesAndTypes = new HashMap<String, StoreableEnum>();
        Map<Class<?>, StoreableEnum> dataBaseClassesAndTypes = new HashMap<Class<?>, StoreableEnum>();
        Set<String> tmp = new HashSet<String>();
        for (StoreableEnum type : values()) {
            dataBaseNamesAndTypes.put(type.nameOfClass, type);
            dataBaseClassesAndTypes.put(type.theClass, type);
        }
        tmp.add("Integer");
        tmp.add("Long");
        tmp.add("Byte");
        tmp.add("Boolean");
        tmp.add("Double");
        tmp.add("Float");
        tmp.add("string");
        tmp.add("Int");
        dataBaseNamesToType = Collections.unmodifiableMap(dataBaseNamesAndTypes);
        dataBaseClassesToType = Collections.unmodifiableMap(dataBaseClassesAndTypes);
        dataBaseCorrectNamesOfClasses = Collections.unmodifiableSet(tmp);
    }

    public static Class<?> getClassByName(String name) {

        name = name.toLowerCase();
        if (name.equals("integer")) {
            name = "int";
        }
        if (name.equals("string")) {
            name = "String";
        }
        StoreableEnum types = dataBaseNamesToType.get(name);
        if (types == null) {
            throw new IllegalArgumentException("wrong type");
        }
        return types.theClass;
    }

    public static String getNameByClass(Class<?> inClass) {

        StoreableEnum types = dataBaseClassesToType.get(inClass);
        if (types == null) {
            throw new IllegalArgumentException("I don't know this type class");
        }
        return types.nameOfClass;
    }

    public static Object parseValueWithClass(String value, Class<?> expectedClass) {

        StoreableEnum types = dataBaseClassesToType.get(expectedClass);
        if (types == null) {
            throw new IllegalArgumentException("I don't know this type");
        }
        return types.parseValue(value);
    }
}
