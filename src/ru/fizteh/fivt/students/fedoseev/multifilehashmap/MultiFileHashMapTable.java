package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTable implements Table {
    private static final int MAX_TABLE_SIZE = 4 * 1024 * 1024;
    private static final int MAX_FILE_SIZE = 1024 * 1024;
    private static final int DIRS_NUMBER = 16;
    private static final int DIR_FILES_NUMBER = 16;

    private String tableName;
    private Map<String, String> contents;
    private Map<String, String> diff;
    private long tableSize;
    private boolean[] boolUsedDirs;
    private boolean[][] boolUsedFiles;
    private int number;
    private int prevNumber;

    public MultiFileHashMapTable(String tableName) {
        this.tableName = tableName;
        tableSize = 0;

        boolUsedDirs = new boolean[DIRS_NUMBER];
        boolUsedFiles = new boolean[DIRS_NUMBER][DIR_FILES_NUMBER];
        contents = new HashMap<>();
        diff = new HashMap<>();

        prevNumber = contents.size();
        number = prevNumber;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) {
        if (key != null) {
            key = key.trim();
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("GET ERROR: incorrect key");
        }

        if (diff.containsKey(key)) {
            return diff.get(key);
        }

        getBoolUsedDirs()[dirHash(key)] = true;
        getBoolUsedFiles()[dirHash(key)][fileHash(key)] = true;

        return contents.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key != null) {
            key = key.trim();
        }
        if (value != null) {
            value = value.trim();
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("PUT ERROR: incorrect key");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("PUT ERROR: incorrect value");
        }

        if (!diff.containsKey(key) && !contents.containsKey(key) || diff.containsKey(key) && diff.get(key) == null) {
            number++;
        }

        getBoolUsedDirs()[dirHash(key)] = true;
        getBoolUsedFiles()[dirHash(key)][fileHash(key)] = true;

        String prevValue = get(key);

        diff.put(key, value);

        if (value.equals(contents.get(key))) {
            diff.remove(key);
        }

        return prevValue;
    }

    @Override
    public String remove(String key) {
        if (key != null) {
            key = key.trim();
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("REMOVE ERROR: incorrect key");
        }

        if (!diff.containsKey(key) && contents.get(key) != null || diff.get(key) != null) {
            number--;
        }

        getBoolUsedDirs()[dirHash(key)] = true;
        getBoolUsedFiles()[dirHash(key)][fileHash(key)] = true;

        String prevValue = get(key);

        diff.put(key, null);

        if (contents.get(key) == null) {
            diff.remove(key);
        }

        return prevValue;
    }

    @Override
    public int size() {
        return number;
    }

    @Override
    public int commit() {
        for (String key : diff.keySet()) {
            if (diff.get(key) == null) {
                contents.remove(key);
            } else {
                contents.put(key, diff.get(key));
            }
        }

        int prevSize = diff.size();

        diff.clear();
        prevNumber = number;

        return prevSize;
    }

    @Override
    public int rollback() {
        int prevSize = diff.size();

        diff.clear();
        number = prevNumber;

        return prevSize;
    }

    public void ifUnfitCurTableSize() throws IOException {
        if (this.getTableSize() > MAX_TABLE_SIZE) {
            AbstractMultiFileHashMap.saveTable(this);
            clearContentAndDiff();
        }
    }

    public void ifUnfitCurFileSize(RandomAccessFile raf) throws IOException {
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
        return diff.size();
    }

    public Map<String, String> getMapContent() {
        return contents;
    }

    public File getCurTableDir() {
        return new File(tableName);
    }

    public void clearContentAndDiff() {
        contents.clear();
        diff.clear();
    }

    public int dirHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) % DIRS_NUMBER;
    }

    public int fileHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) / DIRS_NUMBER % DIR_FILES_NUMBER;
    }

    public void putMapTable(Map<String, String> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                contents.put(key, map.get(key));
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
        for (String key : getMapContent().keySet()) {
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
}
