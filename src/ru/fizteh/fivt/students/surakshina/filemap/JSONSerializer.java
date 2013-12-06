package ru.fizteh.fivt.students.surakshina.filemap;

import org.json.JSONArray;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class JSONSerializer {
    public static String serialize(Table table, Storeable value) {
        if (value == null) {
            return null;
        }
        int numberColumns = table.getColumnsCount();
        Object[] values = new Object[numberColumns];
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values[i] = value.getColumnAt(i);
            if (values[i] != null && !value.getColumnAt(i).getClass().equals(table.getColumnType(i))) {
                throw new ColumnFormatException("Incorrect column name");
            }
        }
        JSONArray array = new JSONArray(values);
        return array.toString();

    }

}
