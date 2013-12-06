package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.*;

public class FileMapUtils {

    static void checkArg(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key or value is clear");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("key cant' be empty");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("key isn't correct");
        }
    }

    static Map<String, Class<?>> mapStringClass()  {
        Map<String, Class<?>> convertList = new HashMap<String, Class<?>>(){ {
            put("int",       Integer.class);
            put("long",      Long.class);
            put("byte",      Byte.class);
            put("float",     Float.class);
            put("double",    Double.class);
            put("boolean",   Boolean.class);
            put("String",    String.class);
        }};
        return convertList;
    }

    static Class<?> convertStringToClass(String type) {
        Map<String, Class<?>> convertList = mapStringClass();
        if (convertList.containsKey(type)) {
            return convertList.get(type);
        } else {
            return null;
        }
    }

    static Map<Class<?>, String> mapClassString()  {
        Map<Class<?>, String> convertList = new HashMap<Class<?>, String>(){ {
            put(Integer.class,  "int");
            put(Long.class,     "long");
            put(Byte.class,     "byte");
            put(Float.class,    "float");
            put(Double.class,   "double");
            put(Boolean.class,  "boolean");
            put(String.class,   "String");
        }};
        return convertList;
    }

    static String convertClassToString(Class<?> type) {
        Map<Class<?>, String> convertList = mapClassString();
        if (convertList.containsKey(type)) {
            return convertList.get(type);
        } else {
            return null;
        }
    }

    public static String getStringFromElement(Storeable storeable, int columnIndex, Class<?> columnType) {
        if (columnType == Integer.class) {
            return Integer.toString(storeable.getIntAt(columnIndex));
        }
        if (columnType == Long.class) {
            return Long.toString(storeable.getLongAt(columnIndex));
        }
        if (columnType == Byte.class) {
            return Byte.toString(storeable.getByteAt(columnIndex));
        }
        if (columnType == Float.class) {
            return Float.toString(storeable.getFloatAt(columnIndex));
        }
        if (columnType == Double.class) {
            return Double.toString(storeable.getDoubleAt(columnIndex));
        }
        if (columnType == Boolean.class) {
            return Boolean.toString(storeable.getBooleanAt(columnIndex));
        }
        if (columnType == String.class) {
            return storeable.getStringAt(columnIndex);
        }
        throw new ColumnFormatException(String.format("wrong column %s", storeable.getIntAt(columnIndex).toString()));
    }

    public static Object parseValue(String s, Class<?> classType) {
        if (classType == Integer.class) {
            return Integer.parseInt(s);
        }
        if (classType == Long.class) {
            return Long.parseLong(s);
        }
        if (classType == Byte.class) {
            return Byte.parseByte(s);
        }
        if (classType == Float.class) {
            return Float.parseFloat(s);
        }
        if (classType == Double.class) {
            return Double.parseDouble(s);
        }
        if (classType == Boolean.class) {
            return Boolean.parseBoolean(s);
        }
        if (classType == String.class) {
            return s;
        }
        throw new ColumnFormatException("wrong column format");
    }

    static int getCode(String s) {
        if (s.charAt(1) == '.') {
            return Integer.parseInt(s.substring(0, 1));
        } else {
            return Integer.parseInt(s.substring(0, 2));
        }
    }

    static String[] myParsing(String[] args) {
        String arg = args[0].trim();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int i = 0;
        while (i < arg.length() && arg.charAt(i) != ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) != ' ') {
            key.append(arg.charAt(i));
            ++i;
        }
        while (i < arg.length() && arg.charAt(i) == ' ') {
            ++i;
        }
        while (i < arg.length()) {
            value.append(arg.charAt(i));
            ++i;
        }
        return new String[]{key.toString(), value.toString()};
    }

    public static void getMessage(Exception e) {
        if (e.getMessage() != null) {
            errPrint(e.getMessage());
        }
        if (e.getSuppressed() != null) {
            for (int i = 0; i < e.getSuppressed().length; ++i) {
                if (e.getSuppressed()[i].getMessage() != null) {
                    errPrint(e.getSuppressed()[i].getMessage());
                }
            }
        }
    }

    public static Boolean antiCorrectDir(String dir) {
        return dir.contains("/") || dir.contains(":") || dir.contains("*")
                || dir.contains("?") || dir.contains("\"") || dir.contains("\\")
                || dir.contains(">") || dir.contains("<") || dir.contains("|");
    }

    public static List<String> parsingForCreate(String[] args) throws IllegalArgumentException {
        String query = args[0];
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();
        if (countTokens < 3) {
            throw new IllegalArgumentException("A few argument");
        }

        List<String> res = new ArrayList<>();
        res.add(token.nextToken());
        res.add(token.nextToken());

        for (int i = 2; i < countTokens; ++i) {
            String t = token.nextToken();
            if (i == 2 && t.trim().charAt(0) != '(') {
                throw new IllegalArgumentException("wrong type ( )");
            }
            if (i == countTokens - 1 && t.trim().charAt(t.trim().length() - 1) != ')') {
                throw new IllegalArgumentException("wrong type ( )");
            }
            if (t.charAt(0) == '(') {
                t = t.substring(1);
            }
            if (t.isEmpty()) {
                continue;
            }
            if (t.charAt(t.length() - 1) == ')') {
                if (t.length() - 1 > 0) {
                    t = t.substring(0, t.length() - 1);
                }
            }
            if (!t.trim().isEmpty() && !t.contains(")") && !t.contains("(")) {
                res.add(t.trim());
            }
        }
        return res;
    }

    static void errPrint(String message) {
        System.err.println(message);
        //System.err.flush();
    }
}
