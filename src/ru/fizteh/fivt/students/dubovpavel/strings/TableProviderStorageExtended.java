package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageException;

import java.io.IOException;

public class TableProviderStorageExtended<DB extends FileRepresentativeDataBase> {
    protected Storage<DB> storage;

    private boolean isNameValid(String name) {
        return !name.contains("\\") && !name.contains("/");
    }

    public TableProviderStorageExtended(Storage storage) {
        this.storage = storage;
    }

    public DB getTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (!isNameValid(name)) {
            throw new RuntimeException();
        }
        return storage.getDataBase(name);
    }

    public DB createTableExplosive(String name) throws IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (!isNameValid(name)) {
            throw new RuntimeException();
        }
        try {
            return storage.createExplosive(name);
        } catch (StorageException e) {
            throw new RuntimeException();
        }
    }

    public void removeTableExplosive(String name) throws IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (storage.dropExplosive(name) == null) {
            throw new IllegalStateException();
        }
    }
}
