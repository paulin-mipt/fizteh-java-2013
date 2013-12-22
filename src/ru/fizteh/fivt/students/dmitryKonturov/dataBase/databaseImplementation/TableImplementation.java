package ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.DatabaseException;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableImplementation implements Table {
    private final String tableName;
    private final TableProviderImplementation tableProvider;
    private final List<Class<?>> columnTypes;
    private final int columnsCount;

    private ThreadLocal<Map<String, Storeable>> currentChangesMap = new ThreadLocal<Map<String, Storeable>>() {
        @Override
        protected Map<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private HashMap<String, Storeable> savedMap;
    private final Path tablePath;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock readLock  = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    /**
     * loads database from its folder
     */
    TableImplementation(String tableName, TableProviderImplementation tableProvider) throws IOException,
            DatabaseException {

        this.tableName = tableName;
        this.tableProvider = tableProvider;
        this.tablePath = tableProvider.getWorkspace().resolve(tableName);
        Path signatureFile = tablePath.resolve(StoreableUtils.getSignatureFileName());
        this.columnTypes = StoreableUtils.loadSignatureFile(signatureFile);
        this.columnsCount = columnTypes.size();
        this.savedMap = new HashMap<>();
        Map<String, String> tmpBase = new HashMap<>();
        MultiFileMapLoaderWriter.loadDatabase(tableProvider.getWorkspace(), tableName, tmpBase);
        for (Map.Entry<String, String> entry : tmpBase.entrySet()) {
            try {
                Storeable storeable = tableProvider.deserialize(this, entry.getValue());
                savedMap.put(entry.getKey(), storeable);
            } catch (Exception e) {
                throw new DatabaseException("Cannot deserialize file", e);
            }

        }

    }

    /**
     * not even try to load database
     */
    TableImplementation(String tableName, TableProviderImplementation tableProvider, List<Class<?>> columnTypes) throws
            IOException, RuntimeException, DatabaseException {

        this.tableName = tableName;
        this.tableProvider = tableProvider;
        this.columnTypes = new ArrayList<>();
        for (Class<?> type : columnTypes) {
            if (type == null) {
                throw new IllegalArgumentException("type can't be null");
            }
            if (!StoreableUtils.isSupportedType(type)) {
                throw new IllegalArgumentException("type no supported");
            }
            this.columnTypes.add(type);
        }
        this.columnsCount = columnTypes.size();
        this.savedMap = new HashMap<>();
        this.tablePath = tableProvider.getWorkspace().resolve(tableName);
        Files.createDirectory(tablePath);
        StoreableUtils.writeSignatureFile(tablePath.resolve(StoreableUtils.getSignatureFileName()),
                                          this.columnTypes);

    }

    private boolean isTableStoreableEqual(Storeable first, Storeable second) {
        if (first == null || second == null) {
            return first == null && second == null;
        }
        boolean result = true;
        for (int i = 0; i < columnsCount; ++i) {
            Object firstObject = first.getColumnAt(i);
            Object secondObject = second.getColumnAt(i);
            if (firstObject == null) {
                result &= (secondObject == null);
            } else {
                result &= secondObject != null && firstObject.equals(secondObject);
            }
        }
        return result;
    }

    public int getUnsavedChangesCount() { //need external sync
        int changesNum = 0;
        for (Map.Entry<String, Storeable> entry : currentChangesMap.get().entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            Storeable savedValue = savedMap.get(key);

            if (value == null) {
                if (savedValue != null) {
                    ++changesNum;
                }
            } else {
                if (savedValue == null) {
                    ++changesNum;
                } else if (!isTableStoreableEqual(value, savedValue)) { // must be true
                    ++changesNum;
                }
            }

        }
        return changesNum;
    }

    private void checkTableState() {
        if (!tableProvider.isProviderLoading() && tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
    }

    private void checkKey(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("key must have positive length");
        }
        if (key.matches("(.*\\s+.*)+")) {
            throw new IllegalArgumentException("key contains space characters");
        }
    }

    @Override
    public String getName() {
        checkTableState();
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Empty key");
        }
        checkTableState();
        if (currentChangesMap.get().containsKey(key)) {
            return currentChangesMap.get().get(key);
        } else {
            readLock.lock();
            try {
                return savedMap.get(key);
            } finally {
                readLock.unlock();
            }
        }

    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Empty value");
        }
        checkTableState();
        StoreableUtils.checkStoreableBelongsToTable(this, value);



        Storeable toReturn = get(key);
        currentChangesMap.get().put(key, value);
        return toReturn;

    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Empty key");
        }
        checkTableState();


        Storeable toReturn = get(key);
        currentChangesMap.get().put(key, null);
        return toReturn;
    }

    @Override
    public int size() {
        checkTableState();
        readLock.lock();
        try {
            int tableSize = savedMap.size();
            for (Map.Entry<String, Storeable> entry : currentChangesMap.get().entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                Storeable savedValue = savedMap.get(key);
                if (savedValue == null) { // Was not saved
                    if (value != null) {
                        ++tableSize;
                    }
                } else {                  // saved
                    if (value == null) {
                        --tableSize;
                    }
                }
            }
            return tableSize;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        checkTableState();
        writeLock.lock();
        try {
            int changesNumber = 0;
            boolean[] changedTableHash = new boolean[16];
            for (Map.Entry<String, Storeable> entry : currentChangesMap.get().entrySet()) {
                String key = entry.getKey();
                int keyHash = Math.abs(key.hashCode());
                Storeable value = entry.getValue();
                Storeable savedValue;
                if (value == null) { // need to remove value
                    savedValue = savedMap.remove(key);
                    changedTableHash[keyHash % 16] = true;
                    if (savedValue != null) { //if contains before
                        ++changesNumber;
                    }
                } else {
                    savedValue = savedMap.put(key, value);
                    changedTableHash[keyHash % 16] = true;
                    if (savedValue == null) { // new key mapping
                        ++changesNumber;
                    } else {                  // only if different values
                        if (!isTableStoreableEqual(savedValue, value)) { //must be false
                            ++changesNumber;
                        }
                    }
                }
            }

            currentChangesMap.get().clear();

            Map<String, String>[] savedStringMap = new HashMap[16];
            for (int i = 0; i < 16; ++i) {
                if (changedTableHash[i]) {
                    savedStringMap[i] = new HashMap<>();
                }
            }
            for (Map.Entry<String, Storeable> entry : savedMap.entrySet()) {
                try {
                    String key = entry.getKey();
                    int keyHash = Math.abs(key.hashCode());
                    if (changedTableHash[keyHash % 16]) {
                        savedStringMap[keyHash % 16].put(key, tableProvider.serialize(this, entry.getValue()));
                    }
                } catch (Exception e) {
                    throw new IOException("Cannot serialize storable");
                }
            }
            try {
                MultiFileMapLoaderWriter.writeMultipleDatabase(tableProvider.getWorkspace(), tableName, savedStringMap);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException(e);
            }
            return changesNumber;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int rollback() {
        checkTableState();
        int toReturn;
        readLock.lock();
        try {
            toReturn = getUnsavedChangesCount();
        } finally {
            readLock.unlock();
        }
        currentChangesMap.get().clear();
        return toReturn;
    }

    @Override
    public int getColumnsCount() {
        checkTableState();
        return columnsCount;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkTableState();
        if (columnIndex < 0) {
            throw new IndexOutOfBoundsException("Negative index");
        }
        if (columnIndex >= columnsCount) {
            throw new IndexOutOfBoundsException("Index bigger than columns count");
        }
        return columnTypes.get(columnIndex);

    }
}
