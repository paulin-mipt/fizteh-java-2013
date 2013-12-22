package ru.fizteh.fivt.students.piakovenko.filemap.storable.JSON;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.piakovenko.filemap.storable.Element;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 10.11.13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class JSONSerializer {
    public static Storeable deserialize(Table table, String value) throws ParseException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(table.getColumnType(i));
        }
        Storeable newStoreable = new Element(columnTypes);
        try {
            JSONArray json = new JSONArray(value);
            Object temp = null;
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                temp = json.get(i);
                newStoreable.setColumnAt(i, temp);
            }
        } catch (JSONException e) {
            System.err.println("Error with JSON!" + e.getMessage());
            return null;
        }
        return newStoreable;
    }

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {
        StringBuilder result = new StringBuilder("{");
        for (Integer i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null) {
                Class<?> clazz = value.getColumnAt(i).getClass();
                if (!clazz.equals(table.getColumnType(i))) {
                    throw new ColumnFormatException("Serialize: wrong class!");
                }
                if (!clazz.equals(String.class)) {
                    result.append("\"");
                    result.append(i.toString());
                    result.append("\":");
                    result.append(value.getColumnAt(i).toString());
                } else {
                    result.append("\"");
                    result.append(i.toString());
                    result.append("\":");
                    result.append(value.getStringAt(i));
                }
            } else {
                result.append("\"");
                result.append(i.toString());
                result.append("\":");
                result.append("null");
            }
            if (i != table.getColumnsCount() - 1) {
                result.append(", ");
            } else {
                result.append("}");
            }
        }
        return result.toString();
    }
}
