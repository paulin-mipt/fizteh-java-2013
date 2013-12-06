package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StoreableTable implements Table {
    private static final int MAX_TABLE_SIZE = 4 * 1024 * 1024;
    private static final int MAX_FILE_SIZE = 1024 * 1024;
    private static final int DIRS_NUMBER = 16;
    private static final int DIR_FILES_NUMBER = 16;

    private String tableName;
    private Map<String, Storeable> contents;
    private long tableSize;
    private boolean[] boolUsedDirs;
    private boolean[][] boolUsedFiles;
    private StoreableTableProvider tp;
    private List<Class<?>> columnTypes;
    private int controlFigures;
    private ThreadLocal<boolean[]> localBoolUsedDirs;
    private ThreadLocal<boolean[][]> localBoolUsedFiles;
    private ThreadLocal<Map<String, Storeable>> diff;
    private ThreadLocal<Map<String, Storeable>> dataAlreadyExists;
    private ThreadLocal<Integer> localControlFigures;
    private ThreadLocal<Integer> number;
    private ReadWriteLock locker;

    public StoreableTable(String tableName, List<Class<?>> columnTypes, StoreableTableProvider prev) {
        this.tableName = tableName;
        tableSize = 0;
        tp = prev;
        boolUsedDirs = new boolean[DIRS_NUMBER];
        boolUsedFiles = new boolean[DIRS_NUMBER][DIR_FILES_NUMBER];
        contents = new HashMap<>();
        localBoolUsedDirs = new ThreadLocal<boolean[]>() {
            @Override
            public boolean[] initialValue() {
                return new boolean[DIRS_NUMBER];
            }
        };
        localBoolUsedFiles = new ThreadLocal<boolean[][]>() {
            @Override
            public boolean[][] initialValue() {
                return new boolean[DIRS_NUMBER][DIR_FILES_NUMBER];
            }
        };
        diff = new ThreadLocal<Map<String, Storeable>>() {
            @Override
            public Map<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };
        dataAlreadyExists = new ThreadLocal<Map<String, Storeable>>() {
            @Override
            public Map<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };
        localControlFigures = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return 0;
            }
        };
        number = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return 0;
            }
        };
        locker = new ReentrantReadWriteLock();
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) {
        checkKeyFormat(key);

        locker.readLock().lock();

        try {
            if (diff.get().containsKey(key)) {
                return diff.get().get(key);
            }

            return contents.get(key);
        } finally {
            locker.readLock().unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        checkKeyFormat(key);
        if (value == null) {
            throw new IllegalArgumentException("PUT ERROR: invalid value");
        }

        try {
            for (int i = 0; i < columnTypes.size(); i++) {
                Object v = value.getColumnAt(i);

                if (v != null && !columnTypes.get(i).equals(v.getClass())) {
                    throw new ColumnFormatException("PUT ERROR: invalid value");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("PUT ERROR: invalid value");
        }

        boolean throwed = false;

        try {
            value.getColumnAt(columnTypes.size());
        } catch (IndexOutOfBoundsException e) {
            throwed = true;
        } finally {
            if (!throwed) {
                throw new ColumnFormatException("PUT ERROR: invalid value");
            }
        }

        locker.readLock().lock();

        try {
            if (!diff.get().containsKey(key) && !contents.containsKey(key)
                    || diff.get().containsKey(key) && diff.get().get(key) == null) {
                number.set(number.get() + 1);
            }

            localBoolUsedDirs.get()[dirHash(key)] = true;
            localBoolUsedFiles.get()[dirHash(key)][fileHash(key)] = true;

            Storeable prevValue = get(key);

            diff.get().put(key, value);

            String stringExistingDataValue = null;
            String stringValue = tp.serialize(this, value);
            String stringContentsValue = null;

            if (dataAlreadyExists.get().containsKey(key)) {
                stringExistingDataValue = tp.serialize(this, dataAlreadyExists.get().get(key));
            }
            if (contents.get(key) != null) {
                stringContentsValue = tp.serialize(this, contents.get(key));
            }

            if (dataAlreadyExists.get().containsKey(key) && !stringValue.equals(stringExistingDataValue)) {
                dataAlreadyExists.get().remove(key);
            }
            if (contents.get(key) != null && stringValue.equals(stringContentsValue)) {
                dataAlreadyExists.get().put(key, value);
                diff.get().remove(key);
            }

            return prevValue;
        } finally {
            locker.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        checkKeyFormat(key);

        locker.readLock().lock();

        try {
            updateControlFigures();

            if (!diff.get().containsKey(key) && contents.get(key) != null || diff.get().get(key) != null) {
                number.set(number.get() - 1);
            }

            localBoolUsedDirs.get()[dirHash(key)] = true;
            localBoolUsedFiles.get()[dirHash(key)][fileHash(key)] = true;

            Storeable prevValue = get(key);

            diff.get().put(key, null);

            if (dataAlreadyExists.get().get(key) != null && dataAlreadyExists.get().containsKey(key)) {
                dataAlreadyExists.get().remove(key);
            }
            if (contents.get(key) == null) {
                dataAlreadyExists.get().put(key, null);
                diff.get().remove(key);
            }

            return prevValue;
        } finally {
            locker.readLock().unlock();
        }
    }

    @Override
    public int size() {
        locker.readLock().lock();

        try {
            updateControlFigures();

            return number.get();
        } finally {
            locker.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        locker.writeLock().lock();

        try {
            updateControlFigures();

            int prevSize = diff.get().size();

            for (String existingDataKey : dataAlreadyExists.get().keySet()) {
                String stringExistingDataValue = null;
                String stringContentsValue = null;

                if (dataAlreadyExists.get().get(existingDataKey) != null) {
                    stringExistingDataValue = tp.serialize(this, dataAlreadyExists.get().get(existingDataKey));
                }
                if (contents.get(existingDataKey) != null) {
                    stringContentsValue = tp.serialize(this, contents.get(existingDataKey));
                }

                if (contents.get(existingDataKey) != null && dataAlreadyExists.get().get(existingDataKey) == null) {
                    prevSize++;

                    contents.remove(existingDataKey);
                } else if (stringContentsValue != null) {
                    if (!stringContentsValue.equals(stringExistingDataValue)
                            && dataAlreadyExists.get().get(existingDataKey) != null) {
                        prevSize++;

                        contents.put(existingDataKey, dataAlreadyExists.get().get(existingDataKey));
                    }
                }
            }
            for (String diffKey : diff.get().keySet()) {
                if (diff.get().get(diffKey) == null) {
                    contents.remove(diffKey);
                } else {
                    contents.put(diffKey, diff.get().get(diffKey));
                }
            }

            updateBoolUsed();
            localControlFigures.set(++controlFigures);

            dataAlreadyExists.get().clear();
            diff.get().clear();

            return prevSize;
        } finally {
            locker.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        locker.readLock().lock();

        try {
            updateControlFigures();

            int prevSize = diff.get().size();

            dataAlreadyExists.get().clear();
            diff.get().clear();
            number.set(contents.size());

            return prevSize;
        } finally {
            locker.readLock().unlock();
        }
    }

    public void checkTable() throws IOException {
        if (this.getTableSize() > MAX_TABLE_SIZE) {
            AbstractStoreable.saveTable(this);
            clearContentAndDiff();
        }
    }

    public void checkFile(RandomAccessFile raf) throws IOException {
        if (raf.length() == 0) {
            raf.close();

            throw new IOException("ERROR: empty file");
        }
        if (raf.length() > MAX_FILE_SIZE) {
            raf.close();

            throw new IOException("ERROR: too big file");
        }
    }

    public int getDirsNumber() {
        return DIRS_NUMBER;
    }

    public int getDirFilesNumber() {
        return DIR_FILES_NUMBER;
    }

    public void setTableSize(long tableSize) {
        this.tableSize = tableSize;
    }

    public long getTableSize() {
        return tableSize;
    }

    public int getDiffSize() {
        return diff.get().size();
    }

    public Map<String, Storeable> getMapContents() {
        return contents;
    }

    public File getCurTableDir() {
        return new File(tableName);
    }

    public void clearContentAndDiff() {
        contents.clear();
        diff.get().clear();
    }

    public int dirHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) % DIRS_NUMBER;
    }

    public int fileHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) / DIRS_NUMBER % DIR_FILES_NUMBER;
    }

    public void putMapTable(Map<String, Storeable> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                put(key, map.get(key));
            }
        }
    }

    public boolean[] getBoolUsedDirs() {
        return boolUsedDirs;
    }

    public boolean[][] getBoolUsedFiles() {
        return boolUsedFiles;
    }

    public void setUsedDirs() {
        for (String key : getMapContents().keySet()) {
            boolUsedDirs[dirHash(key)] = true;
        }
    }

    public void clearUsedDirs() {
        for (int i = 0; i < DIRS_NUMBER; i++) {
            boolUsedDirs[i] = false;
        }
    }

    public void clearUsedFiles() {
        for (int i = 0; i < DIR_FILES_NUMBER; i++) {
            for (int j = 0; j < DIR_FILES_NUMBER; j++) {
                boolUsedFiles[i][j] = false;
            }
        }
    }

    public StoreableTableProvider getTp() {
        return tp;
    }

    private void checkKeyFormat(String key) {
        if (key == null || !key.matches("[\\S]+")) {
            throw new IllegalArgumentException("GET | PUT | REMOVE ERROR: invalid key");
        }
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException("ERROR: invalid column index");
        }

        return columnTypes.get(columnIndex);
    }

    public List<Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public void checkKeyPlacement(String key, File dir, File file) throws IOException {
        String fileName = file.getName();
        int fileNumber = Integer.parseInt(fileName.substring(0, fileName.indexOf('.')));
        String dirName = dir.getName();
        int dirNumber = Integer.parseInt(dirName.substring(0, dirName.indexOf('.')));

        if (fileHash(key) != fileNumber || dirHash(key) != dirNumber) {
            throw new IOException("ERROR: wrong key placement");
        }
    }

    public void updateControlFigures() {
        if (controlFigures != localControlFigures.get()) {
            number.set(contents.size());
            localControlFigures.set(controlFigures);
            updateLocalBoolUsed();
            updateDiff();
        }
    }

    public void updateLocalBoolUsed() {
        for (int i = 0; i < DIRS_NUMBER; i++) {
            localBoolUsedDirs.get()[i] = boolUsedDirs[i];
        }

        for (int i = 0; i < DIRS_NUMBER; i++) {
            for (int j = 0; j < DIR_FILES_NUMBER; j++) {
                localBoolUsedFiles.get()[i][j] = boolUsedFiles[i][j];
            }
        }
    }

    public void updateBoolUsed() {
        for (int i = 0; i < DIRS_NUMBER; i++) {
            boolUsedDirs[i] = localBoolUsedDirs.get()[i];
        }

        for (int i = 0; i < DIRS_NUMBER; i++) {
            for (int j = 0; j < DIR_FILES_NUMBER; j++) {
                boolUsedFiles[i][j] = localBoolUsedFiles.get()[i][j];
            }
        }
    }

    public void updateDiff() {
        Map<String, Storeable> newDiff = new HashMap<>();

        for (String diffKey : diff.get().keySet()) {
            if (diff.get().get(diffKey) == null) {
                if (contents.containsKey(diffKey)) {
                    number.set(number.get() - 1);
                    newDiff.put(diffKey, null);
                } else {
                    diff.get().remove(diffKey);
                }
            } else {
                String diffValue = tp.serialize(this, diff.get().get(diffKey));
                String contentsValue = null;

                if (contents.get(diffKey) != null) {
                    contentsValue = tp.serialize(this, contents.get(diffKey));
                }

                if (!contents.containsKey(diffKey)) {
                    number.set(number.get() + 1);
                }
                if (!contents.containsKey(diffKey)
                        || contents.containsKey(diffKey)
                        && (contents.get(diffKey) == null || !diffValue.equals(contentsValue))) {
                    newDiff.put(diffKey, diff.get().get(diffKey));
                }
            }
        }

        diff.set(newDiff);
    }
}
