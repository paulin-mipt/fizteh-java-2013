package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SingleFileTable implements Table {
    private Map<String, String> table = new HashMap<>();
    private File dbName;

    public SingleFileTable(File dbName) {
        this.dbName = dbName;
    }

    public void loadTable() throws IOException {
        TableEntryReader reader = new TableEntryReader(dbName);
        while (reader.hasNextEntry()) {
            Map.Entry<String, String> entry = reader.readNextEntry();
            table.put(entry.getKey(), entry.getValue());
        }
    }

    public void saveTable() throws IOException {
        TableEntryWriter writer = new TableEntryWriter(dbName);
        writer.writeEntries(table);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String get(String key) {
        return table.get(key);
    }

    @Override
    public String put(String key, String value) {
        return table.put(key, value);
    }

    @Override
    public String remove(String key) {
        return table.remove(key);
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("size operation is not supported");
    }

    @Override
    public int commit() {
        throw new UnsupportedOperationException("commit operation is not supported");
    }

    @Override
    public int rollback() {
        throw new UnsupportedOperationException("rollback operation is not supported");
    }


}
