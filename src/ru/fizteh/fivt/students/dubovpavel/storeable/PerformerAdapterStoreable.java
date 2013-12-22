package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.filemap.Serial;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

import java.text.ParseException;

public class PerformerAdapterStoreable implements DataBaseHandler<String, String> {
    private Storage<TableStoreable> storage;

    public PerformerAdapterStoreable(Storage<TableStoreable> storage) {
        this.storage = storage;
    }

    private String safeSerializedReturn(TableStoreable table, Storeable obj) throws Serial.SerialException {
        if (obj == null) {
            return null;
        } else {
            return table.getTransformer().serialize(obj);
        }
    }

    public PerformerAdapterStoreable checked() {
        if (storage.getCurrent() != null) {
            return this;
        } else {
            return null;
        }
    }

    public String put(String key, String value) throws DataBaseException {
        TableStoreable table = storage.getCurrent();
        try {
            Storeable old = table.put(key, table.getTransformer().deserialize(value)); // Double check here
            return safeSerializedReturn(table, old);
        } catch (Serial.SerialException e) {
            throw new DataBaseException(String.format("SerialException: %s", e.getMessage()));
        } catch (ParseException e) {
            throw new DataBaseException(String.format("ParseException: %s", e.getMessage()));
        }
    }

    public String get(String key) throws DataBaseException {
        TableStoreable table = storage.getCurrent();
        try {
            Storeable value = table.get(key);
            return safeSerializedReturn(table, value);
        } catch (Serial.SerialException e) {
            throw new DataBaseException(String.format("SerialException: %s", e.getMessage()));
        } catch (ColumnFormatException e) {
            throw new DataBaseException(String.format("ColumnFormatException: %s", e.getMessage()));
        }
    }

    public void save() throws DataBaseException {
        storage.getCurrent().save();
    }

    public void open() throws DataBaseException {
        storage.getCurrent().open();
    }

    public String remove(String key) throws DataBaseException {
        TableStoreable table = storage.getCurrent();
        try {
            Storeable value = table.remove(key);
            return safeSerializedReturn(table, value);
        } catch (Serial.SerialException e) {
            throw new DataBaseException(String.format("SerialException: %s", e.getMessage()));
        } catch (ColumnFormatException e) {
            throw new DataBaseException(String.format("ColumnFormatException: %s", e.getMessage()));
        }
    }
}
