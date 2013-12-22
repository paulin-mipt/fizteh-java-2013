package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

public interface StorageAccessible<S extends Storage> {
    S getStorage();
}
