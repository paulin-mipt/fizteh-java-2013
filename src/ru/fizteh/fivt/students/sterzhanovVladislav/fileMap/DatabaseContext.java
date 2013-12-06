package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.storage.structured.Storeable;

public class DatabaseContext implements Closeable {
    private FileMapProvider provider = null;
    private FileMap activeMap = null;

    public String remove(String key) throws Exception {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        Storeable oldValue = activeMap.remove(key);
        if (oldValue == null) {
            return null;
        }
        return provider.serialize(activeMap, oldValue);
    }

    public String get(String key) throws Exception {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        Storeable value = activeMap.get(key);
        if (value == null) {
            return null;
        }
        return provider.serialize(activeMap, value);
    }

    public String put(String key, String value) throws Exception {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        Storeable oldValue = activeMap.put(key, provider.deserialize(activeMap, value));
        if (oldValue == null) {
            return null;
        }
        return provider.serialize(activeMap, oldValue);
    }
    
    public int commit() throws IOException {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.commit();
    }
    
    public int rollback() {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.rollback();
    }
    
    public int getActiveSize() {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.size();
    }
    
    public void loadTable(String dbName) throws IllegalStateException, IOException {
        if (activeMap != null && activeMap.isDirty()) {
            throw new IllegalStateException(activeMap.getDiffSize() + " unsaved changes");
        }
        FileMap newMap = provider.getTable(dbName);
        if (newMap == null) {
            throw new IllegalStateException(dbName + " not exists");
        }
        closeActiveTable();
        activeMap = newMap;
    }
    
    public void createTable(String dbName, List<Class<?>> signature) throws IllegalStateException {
        try {
            FileMap newMap = provider.createTable(dbName, signature);
            if (newMap == null) {
                throw new IllegalStateException(dbName + " exists");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create directory");
        }
    }
    
    public void removeTable(String dbName) throws IllegalStateException {
        provider.removeTable(dbName);
        if (activeMap != null && !activeMap.isAlive()) {
            activeMap = null;
        }
    }
    
    public void closeActiveTable() throws IOException {
        if (activeMap != null) {
            activeMap.commit();
            activeMap = null;
        }
    }
    
    public DatabaseContext(String path) throws IllegalStateException, IOException {
        provider = new FileMapProvider(path);
    } 
    
    public void close() throws IOException {
        closeActiveTable();
    }
}
