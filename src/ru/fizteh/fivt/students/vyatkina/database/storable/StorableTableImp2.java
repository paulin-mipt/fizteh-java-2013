package ru.fizteh.fivt.students.vyatkina.database.storable;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vyatkina.database.StorableTable;
import ru.fizteh.fivt.students.vyatkina.database.logging.CloseState;
import ru.fizteh.fivt.students.vyatkina.database.superior.DatabaseUtils;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableChecker;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.createFileForKeyIfNotExists;
import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.fileForKey;

public class StorableTableImp2 implements StorableTable {

    private final String name;
    private final StorableTableProviderImp tableProvider;
    private final StorableRowShape shape;
    private volatile Map<String, Storeable> mainMap;
    private ThreadLocal<Map<String, Storeable>> localMap = new ThreadLocal<Map<String, Storeable>>() {
        protected Map<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private final ReadWriteLock tableKeeper = new ReentrantReadWriteLock(true);
    private final CloseState closeState;

    public StorableTableImp2(String name, StorableRowShape shape, StorableTableProviderImp tableProvider) {
        this.name = name;
        this.shape = shape;
        this.tableProvider = tableProvider;
        this.closeState = new CloseState(this + " is closed");
    }

    @Override
    public String getName() {
        closeState.isClosedCheck();
        return name;
    }

    @Override
    public Storeable get(String key) {
        closeState.isClosedCheck();
        TableChecker.keyValidCheck(key);
        tableKeeper.readLock().lock();
        try {
            if (localMap.get().containsKey(key)) {
                return localMap.get().get(key);
            } else {
                return mainMap.get(key);
            }
        }
        finally {
            tableKeeper.readLock().unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        closeState.isClosedCheck();
        TableChecker.keyValidCheck(key);
        TableChecker.valueIsNullCheck(value);
        TableProviderChecker.storableForThisTableCheck(this, value);

        Storeable oldValue = get(key);
        localMap.get().put (key,value);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        closeState.isClosedCheck();
        TableChecker.keyValidCheck(key);
        Storeable oldValue = null;
        try {
            tableKeeper.readLock().lock();
            if (localMap.get().containsKey(key)) {
                oldValue = localMap.get().get(key);
            } else if (mainMap.containsKey(key)) {
                oldValue = mainMap.get(key);
            }
        }
        finally {
            tableKeeper.readLock().unlock();
        }
        localMap.get().put(key, null);
        return oldValue;
    }

    @Override
    public int size() {
        closeState.isClosedCheck();
        tableKeeper.readLock().lock();
        try {
            int size = mainMap.size();
            for (Map.Entry<String, Storeable> entry : localMap.get().entrySet()) {
                if (!mainMap.containsKey(entry.getKey())) {
                    if (entry.getValue() != null) {
                        ++size;
                    }
                } else {
                    if (entry.getValue() == null) {
                        --size;
                    }
                }
            }
            return size;
        }
        finally {
            tableKeeper.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        closeState.isClosedCheck();
        Map<Path, List<DatabaseUtils.KeyValue>> databaseChanges = new HashMap<>();
        Path tableLocation = tableProvider.tableDirectory(name);
        tableKeeper.writeLock().lock();
        try {
            int commitChanges = difference();
            Set<Path> filesChanged = mergeTables();
            for (Path file : filesChanged) {
                Files.deleteIfExists(file);
                databaseChanges.put(file, new ArrayList<DatabaseUtils.KeyValue>());
            }

            for (Map.Entry<String, Storeable> entry : mainMap.entrySet()) {
                Path fileKeyIn = fileForKey(entry.getKey(), tableLocation);
                if (filesChanged.contains(fileKeyIn)) {
                    createFileForKeyIfNotExists(entry.getKey(), tableLocation);
                    String value = tableProvider.serialize(this, entry.getValue());
                    DatabaseUtils.KeyValue keyValue = new DatabaseUtils.KeyValue(entry.getKey(), value);
                    databaseChanges.get(fileKeyIn).add(keyValue);
                }
            }

            TableProviderUtils.writeTable(databaseChanges);
            return commitChanges;
        }
        finally {
            tableKeeper.writeLock().unlock();
        }
    }

    private Set<Path> mergeTables() {
        Set<Path> filesChanged = new HashSet<>();
        Path tableLocation = tableProvider.tableDirectory(name);
        for (Map.Entry<String, Storeable> entry : localMap.get().entrySet()) {
            if (mainMap.containsKey(entry.getKey()) && !mainMap.get(entry.getKey()).equals(entry.getValue())) {
                if (entry.getValue() != null) {
                    mainMap.put(entry.getKey(), entry.getValue());
                } else {
                    mainMap.remove(entry.getKey());
                }
                filesChanged.add(fileForKey(entry.getKey(), tableLocation));
            } else {
                if (entry.getValue() != null) {
                    mainMap.put(entry.getKey(), entry.getValue());
                    filesChanged.add(fileForKey(entry.getKey(), tableLocation));
                }
            }
        }
        localMap.get().clear();
        return filesChanged;
    }

    @Override
    public int rollback() {
        closeState.isClosedCheck();
        int rollbackSize;
        tableKeeper.readLock().lock();
        try {
            rollbackSize = difference();
        }
        finally {
            tableKeeper.readLock().unlock();
        }
        localMap.get().clear();
        return rollbackSize;
    }

    private int difference() {
        int diff = 0;
        for (Map.Entry<String, Storeable> entry : localMap.get().entrySet()) {
            if (mainMap.containsKey(entry.getKey())) {
                if (!mainMap.get(entry.getKey()).equals(entry.getValue())) {
                    ++diff;
                }
            } else {
                if (entry.getValue() != null) {
                    ++diff;
                }
            }
        }
        return diff;
    }

    @Override
    public int unsavedChanges() {
        closeState.isClosedCheck();
        tableKeeper.readLock().lock();
        try {
            return difference();
        }
        finally {
            tableKeeper.readLock().unlock();
        }
    }

    @Override
    public void putValuesFromDisk(Map<String, Storeable> diskValues) {
        closeState.isClosedCheck();
        try {
            tableKeeper.writeLock().lock();
            this.mainMap = diskValues;
        }
        finally {
            tableKeeper.writeLock().unlock();
        }
    }

    @Override
    public int getColumnsCount() {
        closeState.isClosedCheck();
        return shape.getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        closeState.isClosedCheck();
        return shape.getColumnType(columnIndex);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + tableProvider.tableDirectory(name) + "]";
    }

    @Override
    public void close() throws IOException {
        if (closeState.isAlreadyClosed()) {
            return;
        }
        rollback();
        tableProvider.removeOldReference(this);
        closeState.close();
    }

}
