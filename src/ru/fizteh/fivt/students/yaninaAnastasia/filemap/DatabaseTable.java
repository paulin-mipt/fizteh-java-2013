package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseTable implements Table {
    public HashMap<String, Storeable> oldData;
    public ThreadLocal<HashMap<String, Storeable>> modifiedData;
    public ThreadLocal<HashSet<String>> deletedKeys;
    public ThreadLocal<Integer> size;
    public ThreadLocal<Integer> uncommittedChanges;
    private String tableName;
    public List<Class<?>> columnTypes;
    DatabaseTableProvider provider;
    private Lock transactionLock = new ReentrantLock(true);


    public DatabaseTable(String name, List<Class<?>> colTypes, DatabaseTableProvider providerRef) {
        this.tableName = name;
        oldData = new HashMap<String, Storeable>();
        modifiedData = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<String, Storeable>();
            }
        };
        deletedKeys = new ThreadLocal<HashSet<String>>() {
            @Override
            public HashSet<String> initialValue() {
                return new HashSet<String>();
            }
        };
        uncommittedChanges = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return new Integer(0);
            }
        };
        size = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return oldData.size();
            }
        };
        columnTypes = colTypes;
        provider = providerRef;
        uncommittedChanges.set(0);
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null || ColumnTypes.fromTypeToName(columnType) == null) {
                throw new IllegalArgumentException("unknown column type");
            }
        }
    }

    public static int getDirectoryNum(String key) {
        int keyByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        return keyByte % 16;
    }

    public static int getFileNum(String key) {
        int keyByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        return (keyByte / 16) % 16;
    }

    public String getName() {
        if (tableName == null) {
            throw new IllegalArgumentException("Table name cannot be null");
        }
        return tableName;
    }

    public void putName(String name) {
        this.tableName = name;
    }

    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null || (key.isEmpty() || key.trim().isEmpty())) {
            throw new IllegalArgumentException("Table name cannot be null");
        }
        if (modifiedData.get().containsKey(key)) {
            return modifiedData.get().get(key);
        }
        if (deletedKeys.get().contains(key)) {
            return null;
        }
        return oldData.get(key);
    }

    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty())) {
            throw new IllegalArgumentException("Key can not be null");
        }
        if (key.matches("\\s*") || key.split("\\s+").length != 1) {
            throw new IllegalArgumentException("Key contains whitespaces");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        checkAlienStoreable(value);
        for (int index = 0; index < getColumnsCount(); ++index) {
            switch (ColumnTypes.fromTypeToName(columnTypes.get(index))) {
                case "String":
                    String stringValue = (String) value.getColumnAt(index);
                    if (stringValue == null) {
                        continue;
                    }
                    break;
                default:
            }
        }
        Storeable oldValue = null;
        oldValue = modifiedData.get().get(key);
        if (oldValue == null && !deletedKeys.get().contains(key)) {
            oldValue = oldData.get(key);
        }
        modifiedData.get().put(key, value);
        if (deletedKeys.get().contains(key)) {
            deletedKeys.get().remove(key);
        }
        if (oldValue == null) {
            size.set(size.get() + 1);
        }
        uncommittedChanges.set(changesCount());
        return oldValue;
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null || (key.isEmpty() || key.trim().isEmpty())) {
            throw new IllegalArgumentException("Key name cannot be null");
        }
        Storeable oldValue = null;
        oldValue = modifiedData.get().get(key);
        if (oldValue == null && !deletedKeys.get().contains(key)) {
            oldValue = oldData.get(key);
        }
        if (modifiedData.get().containsKey(key)) {
            modifiedData.get().remove(key);
            if (oldData.containsKey(key)) {
                deletedKeys.get().add(key);
            }
        } else {
            deletedKeys.get().add(key);
        }
        if (oldValue != null) {
            size.set(size.get() - 1);
        }
        uncommittedChanges.set(changesCount());
        return oldValue;
    }

    public int size() {
        return oldData.size() + diffSize();
    }

    public int commit() {
        int recordsCommitted = 0;
        try {
            transactionLock.lock();
            recordsCommitted = Math.abs(changesCount());
            for (String keyToDelete : deletedKeys.get()) {
                oldData.remove(keyToDelete);
            }
            for (String keyToAdd : modifiedData.get().keySet()) {
                if (modifiedData.get().get(keyToAdd) != null) {
                    oldData.put(keyToAdd, modifiedData.get().get(keyToAdd));
                }
            }
            deletedKeys.get().clear();
            modifiedData.get().clear();
            size.set(oldData.size());
            TableBuilder tableBuilder = new TableBuilder(provider, this);
            save(tableBuilder);
            uncommittedChanges.set(0);
        } finally {
            transactionLock.unlock();
        }
        return recordsCommitted;
    }

    public int rollback() {
        int recordsDeleted = Math.abs(changesCount());

        deletedKeys.get().clear();
        modifiedData.get().clear();
        size.set(oldData.size());

        uncommittedChanges.set(0);

        return recordsDeleted;
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    public Storeable storeableGet(String key) {
        return oldData.get(key);
    }

    public void storeablePut(String key, Storeable value) {
        oldData.put(key, value);
    }

    public boolean save(TableBuilder tableBuilder) {
        if (oldData == null) {
            return true;
        }
        if (tableName.equals("")) {
            return true;
        }
        File tablePath = new File(System.getProperty("fizteh.db.dir"), tableName);
        for (int i = 0; i < 16; i++) {
            String directoryName = String.format("%d.dir", i);
            File path = new File(tablePath, directoryName);
            boolean isDirEmpty = true;
            ArrayList<HashSet<String>> keys = new ArrayList<HashSet<String>>(16);
            for (int j = 0; j < 16; j++) {
                keys.add(new HashSet<String>());
            }
            for (String step : oldData.keySet()) {
                int nDirectory = getDirectoryNum(step);
                if (nDirectory == i) {
                    int nFile = getFileNum(step);
                    keys.get(nFile).add(step);
                    isDirEmpty = false;
                }
            }

            if (isDirEmpty) {
                try {
                    if (path.exists()) {
                        DatabaseTableProvider.recRemove(path);
                    }
                } catch (IOException e) {
                    return false;
                }
                continue;
            }
            if (path.exists()) {
                File file = path;
                try {
                    if (!DatabaseTableProvider.recRemove(file)) {
                        System.err.println("File was not deleted");
                        return false;
                    }
                } catch (IOException e) {
                    return false;
                }
            }
            if (!path.mkdir()) {
                return false;
            }
            for (int j = 0; j < 16; j++) {
                File filePath = new File(path, String.format("%d.dat", j));
                try {
                    saveTable(keys.get(j), filePath.toString(), tableBuilder);
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean saveTable(Set<String> keys, String path, TableBuilder tableBuilder) throws IOException {
        if (keys.isEmpty()) {
            try {
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                return false;
            }
            return false;
        }
        try (RandomAccessFile temp = new RandomAccessFile(path, "rw")) {
            long offset = 0;
            temp.setLength(0);
            for (String step : keys) {
                offset += step.getBytes(StandardCharsets.UTF_8).length + 5;
            }
            for (String step : keys) {
                byte[] bytesToWrite = step.getBytes(StandardCharsets.UTF_8);
                temp.write(bytesToWrite);
                temp.writeByte(0);
                temp.writeInt((int) offset);
                String myOffset = tableBuilder.get(step);
                offset += myOffset.getBytes(StandardCharsets.UTF_8).length;
            }
            for (String key : keys) {
                String value = tableBuilder.get(key);
                temp.write(value.getBytes(StandardCharsets.UTF_8));
            }
            temp.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private int changesCount() {
        int result = 0;
        for (final String key : modifiedData.get().keySet()) {
            if (!modifiedData.get().get(key).equals(oldData.get(key))) {
                result += 1;
            }
        }
        for (final String key : deletedKeys.get()) {
            if (oldData.containsKey(key)) {
                result -= 1;
            }
        }
        return result;
    }

    private int diffSize() {
        int result = 0;
        for (final String key : modifiedData.get().keySet()) {
            Storeable oldValue = oldData.get(key);
            Storeable newValue = modifiedData.get().get(key);
            if (oldValue == null && newValue != null) {
                result += 1;
            }
        }
        return result - deletedKeys.get().size();
    }

    private boolean compare(Storeable key1, Storeable key2) {
        if (key1 == null && key2 == null) {
            return true;
        }
        if (key1 == null || key2 == null) {
            return false;
        }
        return key1.equals(key2);
    }

    public int getColumnsCount() {
        return columnTypes.size();
    }

    public void checkAlienStoreable(Storeable storeable) {
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                Object o = storeable.getColumnAt(index);
                if (o == null) {
                    continue;
                }
                if (!o.getClass().equals(getColumnType(index))) {
                    throw new ColumnFormatException("Alien storeable with incompatible types");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Alien storeable with less columns");
            }
        }
        try {
            storeable.getColumnAt(getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        throw new ColumnFormatException("Alien storeable with more columns");
    }
}
