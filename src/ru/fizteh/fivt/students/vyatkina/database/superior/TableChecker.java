package ru.fizteh.fivt.students.vyatkina.database.superior;

public class TableChecker {

    public static final String KEY_SHOULD_NOT_BE_NULL = "Key should not be null";
    public static final String VALUE_SHOULD_NOT_BE_NULL = "Value should not be null";
    public static final String KEY_SHOULD_NOT_BE_EMPTY = "Key should not be empty";
    public static final String VALUE_SHOULD_NOT_BE_EMPTY = "Value should not be empty";
    public static final String KEY_SHOULD_NOT_CONTAIN_WHITE_SPACES = "Key should not contain white spaces";


    public static void keyValidCheck(String key) {
        if (key == null) {
            throw new IllegalArgumentException(KEY_SHOULD_NOT_BE_NULL);
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException(KEY_SHOULD_NOT_BE_EMPTY);
        }
        if (key.matches(".*[\\s].*")) {
            throw new IllegalArgumentException(KEY_SHOULD_NOT_CONTAIN_WHITE_SPACES);
        }
    }

    public static void valueIsNullCheck(Object value) {
        if (value == null) {
            throw new IllegalArgumentException(VALUE_SHOULD_NOT_BE_NULL);
        }
    }

    public static void stringWhiteSpaceCheck(String string) {
        if (string.trim().isEmpty()) {
            throw new IllegalArgumentException(VALUE_SHOULD_NOT_BE_EMPTY);
        }
    }

    public static void stringValueIsValidCheck(String value) {
        valueIsNullCheck(value);
        stringWhiteSpaceCheck(value);
    }

}
