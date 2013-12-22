package ru.fizteh.fivt.students.inaumov.filemap;

public class FileMapUtils {
    public static boolean isStringNullOrEmpty(String string) {
        if (string == null || string.trim().isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isEqual(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }
}
