package ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.text.ParseException;

public class JsonUtils {

    public static Storeable deserialize(TableProvider tableProvider, Table table, String value) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (tableProvider == null) {
            throw new IllegalArgumentException("tableProvider is null");
        }
        if (table == null) {
            throw new IllegalArgumentException("table is null");
        }

        JSONArray array;
        try {
            array = new JSONArray(value);
        } catch (Exception e) {
            throw new ParseException("Not an array", 0);
        }
        Storeable toReturn;
        try {
            toReturn = tableProvider.createFor(table);
        } catch (Exception e) {
            throw new ParseException("Cannot createFor storeable", 0);
        }
        for (int i = 0; i < array.length(); ++i) {
            try {
                Class<?> type = table.getColumnType(i);
                Object toSet;
                if (array.isNull(i)) {
                    toSet = null;
                } else if (type == Integer.class) {
                    toSet = array.getInt(i);
                } else if (type == Long.class) {
                    toSet = array.getLong(i);
                } else if (type == Boolean.class) {
                    toSet = array.getBoolean(i);
                } else if (type == String.class) {
                    toSet = array.getString(i);
                } else if (type == Double.class) {
                    toSet = array.getDouble(i);
                } else if (type == Float.class) {
                    toSet = (float) array.getDouble(i);
                } else if (type == Byte.class) {
                    toSet = (byte) array.getInt(i);
                } else {
                    throw new ColumnFormatException("Not supported type");
                }
                toReturn.setColumnAt(i, toSet);
            } catch (Exception e) {
                throw new ParseException(e.toString(), i);
            }
        }
        return toReturn;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            StoreableUtils.checkStoreableBelongsToTable(table, value);
        } catch (Exception e) {
            ColumnFormatException parseException = new ColumnFormatException("Wrong parametrs");
            parseException.addSuppressed(e);
            throw parseException;
        }
        JSONArray array = new JSONArray();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            try {
                array.put(value.getColumnAt(i));
            } catch (Exception e) {
                ColumnFormatException parseException = new ColumnFormatException("Wrong parametrs");
                parseException.addSuppressed(e);
                throw parseException;
            }
        }
        return array.toString();
    }

}
