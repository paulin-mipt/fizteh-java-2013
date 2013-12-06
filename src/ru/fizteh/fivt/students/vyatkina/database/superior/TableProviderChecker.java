package ru.fizteh.fivt.students.vyatkina.database.superior;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class TableProviderChecker implements TableProviderConstants {

    public static boolean isValidDatabaseFileName(String name) {
        return Pattern.matches("([0-9]|(1[0-5]))\\.dat", name);
    }

    public static boolean isValidDatabaseDirectoryName(String name) {
        return Pattern.matches("([0-9]|(1[0-5]))\\.dir", name);
    }

    public static void validTableNameCheck(String tableName) throws IllegalArgumentException {
        if ((tableName == null) || (tableName.length() > MAX_SUPPORTED_NAME_LENGTH)) {
            throw new IllegalArgumentException(UNSUPPORTED_TABLE_NAME);
        }
        if (!Pattern.matches("[a-zA-Zа-яА-Я0-9]+", tableName)) {
            throw new IllegalArgumentException(UNSUPPORTED_TABLE_NAME);
        }
        if (tableName.trim().isEmpty()) {
            throw new IllegalArgumentException(UNSUPPORTED_TABLE_NAME);
        }

    }

    public static void isFileCheck(Path path) throws IllegalArgumentException {
        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException(path + IS_NOT_A_FILE);
        }
    }

    public static boolean correctFileForKey(String key, Path dir, Path file) {
        return file.equals(TableProviderUtils.fileForKey(key, dir));
    }

    public static void storableForThisTableCheck(Table table, Storeable storeable) {
        if (table == null || storeable == null) {
            throw new IllegalArgumentException("Null table or storeable");
        }

        for (int i = 0; i < table.getColumnsCount(); i++) {
            try {
                if (storeable.getColumnAt(i) != null) {
                    if (!storeable.getColumnAt(i).equals
                            (getColumnFromTypeAt(i, table.getColumnType(i), storeable))) {
                        throw new ColumnFormatException();
                    }
                }
            }
            catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException(e);
            }
        }

        try {
            storeable.getColumnAt(table.getColumnsCount());
        }
        catch (IndexOutOfBoundsException e) {
            return;
        }
        throw new ColumnFormatException("too big storeable");

    }

    public static Object getColumnFromTypeAt(int columnIndex, Class<?> columnType, Storeable storable)
            throws IndexOutOfBoundsException, ColumnFormatException {

        if (columnType.equals(Integer.class)) {
            return storable.getIntAt(columnIndex);
        } else if (columnType.equals(Long.class)) {
            return storable.getLongAt(columnIndex);
        } else if (columnType.equals(Byte.class)) {
            return storable.getByteAt(columnIndex);
        } else if (columnType.equals(Float.class)) {
            return storable.getFloatAt(columnIndex);
        } else if (columnType.equals(Double.class)) {
            return storable.getDoubleAt(columnIndex);
        } else if (columnType.equals(Boolean.class)) {
            return storable.getBooleanAt(columnIndex);
        } else if (columnType.equals(String.class)) {
            return storable.getStringAt(columnIndex);
        } else {
            throw new IllegalArgumentException("unsupported object type");
        }
    }

}
