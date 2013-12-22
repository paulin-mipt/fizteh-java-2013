package ru.fizteh.fivt.students.baldindima.junit;

import org.json.JSONArray;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class JSONClass {

    public static Storeable deserialize(Table table, String value) {
        if ((value == null) || (value.length() <= 0)) {
            return null;
        }
        JSONArray jsonValue = new JSONArray(value);
        Storeable result = new BaseStoreable(table);

        for (Integer i = 0; i < jsonValue.length(); ++i) {
            result.setColumnAt(i, jsonValue.get(i));
        }
        return result;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {

        if (!BaseStoreable.isCorrectStoreable(value, table)) {
            throw new ColumnFormatException("Incorrect storeable");
        }


        JSONArray jsonValue = new JSONArray();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            try {
                if (value.getColumnAt(i) == null || value.getColumnAt(i).getClass() == table.getColumnType(i)) {
                    jsonValue.put(value.getColumnAt(i));
                } else {
                    throw new ColumnFormatException("Column has wrong type!");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Too few columns!");
            }
        }
        return jsonValue.toString();
    }


}