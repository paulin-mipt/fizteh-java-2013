package ru.fizteh.fivt.students.adanilyak.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;

/**
 * User: Alexander
 * Date: 25.10.13
 * Time: 14:12
 */
public class FileMapGlobalState {
    public Table currentTable = null;
    public boolean autoCommitOnExit;

    public FileMapGlobalState() {
    }

    public FileMapGlobalState(Table table) {
        currentTable = table;
        autoCommitOnExit = true;
    }

    public String getCurrentTable() {
        if (currentTable == null) {
            return null;
        } else {
            return currentTable.getName();
        }
    }

    public String put(String key, String value) {
        return currentTable.put(key, value);
    }

    public String get(String key) {
        return currentTable.get(key);
    }

    public String remove(String key) {
        return currentTable.remove(key);
    }

    public int commit() throws IOException {
        return currentTable.commit();
    }

    public int rollback() {
        return currentTable.rollback();
    }

    public int size() {
        return currentTable.size();
    }

}
