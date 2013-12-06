package ru.fizteh.fivt.students.piakovenko.filemap.storable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 25.11.13
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class Checker {
    private static final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9]+";

    public static void stringNotEmpty(String value) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("String is invalid");
        }
    }

    public static void correctTableName(String name) throws RuntimeException {
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new RuntimeException("incorrect table name");
        }
    }

    public static void checkColumnTypes(List<Class<?>> columnTypes) throws  IllegalArgumentException {
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("column types cannot be null");
        }

        for (final Class<?> columnType : columnTypes) {
            if (columnType == null || ColumnTypes.fromTypeToName(columnType) == null) {
                throw new IllegalArgumentException("unknown column type");
            }
        }
    }

    public static void equalSizes(int firstSize, int secondSize) throws IndexOutOfBoundsException {
        if (firstSize != secondSize) {
            throw new IndexOutOfBoundsException(firstSize + " is not equal to " + secondSize);
        }
    }

    public static void keyFormat(String key) throws IllegalArgumentException {
        if (key.matches("\\s*") || key.split("\\s+").length != 1) {
            throw new IllegalArgumentException("Key contains whitespaces");
        }
    }

    public static boolean isValidFileNumber(String name) {
        if (name.length() < 5 || name.length() > 6) {
            return false;
        }
        int number = Integer.parseInt(name.substring(0, name.indexOf('.')), 10);
        if (number > 15 || number < 0) {
            return false;
        }
        if (!name.substring(name.indexOf('.') + 1).equals("dir")) {
            return false;
        }
        return true;
    }
}
