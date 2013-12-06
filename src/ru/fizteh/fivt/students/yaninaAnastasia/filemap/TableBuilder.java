package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.text.ParseException;

public class TableBuilder {
    DatabaseTableProvider provider;
    DatabaseTable table;

    public TableBuilder(DatabaseTableProvider provider, DatabaseTable table) {
        this.provider = provider;
        this.table = table;
    }

    public String get(String key) {
        Storeable value = table.storeableGet(key);
        try {
            String str = provider.serialize(table, value);
            return str;
        } catch (ColumnFormatException e) {
            return null;
        }
    }

    public void put(String key, String value) {
        Storeable storage = null;
        try {
            storage = provider.deserialize(table, value);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        table.storeablePut(key, storage);
    }
}
