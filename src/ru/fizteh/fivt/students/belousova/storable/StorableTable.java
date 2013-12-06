package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.belousova.multifilehashmap.AbstractTable;
import ru.fizteh.fivt.students.belousova.utils.StorableUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StorableTable extends AbstractTable<String, Storeable> implements ChangesCountingTable {
    private List<Class<?>> columnTypes = new ArrayList<>();
    StorableTableProvider tableProvider = null;

    public StorableTable(File directory, StorableTableProvider tableProvider) throws IOException {
        dataDirectory = directory;
        this.tableProvider = tableProvider;
        File signatureFile = new File(directory, "signature.tsv");
        StorableUtils.readSignature(signatureFile, columnTypes);
        StorableUtils.readTable(directory, this, dataBase, tableProvider);
        addedKeys = new ThreadLocal<Map<String, Storeable>>() {
            @Override
            public Map<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };
        deletedKeys = new ThreadLocal<Set<String>>() {
            @Override
            public Set<String> initialValue() {
                return new HashSet<>();
            }
        };
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty value");
        }

        if (!StorableUtils.isStorableValid(value, columnTypes)) {
            throw new ColumnFormatException("wrong storeable format");
        }

        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("key with whitespaces");
        }

        return super.put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("key with whitespaces");
        }
        return super.remove(key);
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("key with whitespaces");
        }
        return super.get(key);
    }

    @Override
    public int commit() throws IOException {
        tableTransactionsLock.lock();
        try {
            int counter = countChanges();
            for (String key : deletedKeys.get()) {
                dataBase.remove(key);
            }
            dataBase.putAll(addedKeys.get());
            deletedKeys.get().clear();
            addedKeys.get().clear();
            StorableUtils.writeTable(dataDirectory, this, dataBase, tableProvider);
            return counter;
        } finally {
            tableTransactionsLock.unlock();
        }
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypes.get(columnIndex);
    }
}
