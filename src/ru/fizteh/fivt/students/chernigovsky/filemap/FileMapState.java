package ru.fizteh.fivt.students.chernigovsky.filemap;

import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedMultiFileHashMapTable;
import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedMultiFileHashMapTableProvider;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.MultiFileHashMapUtils;
import java.text.ParseException;

import java.io.IOException;

public class FileMapState implements State {
    private ExtendedMultiFileHashMapTable currentTable;
    private ExtendedMultiFileHashMapTableProvider currentTableProvider;

    public FileMapState(ExtendedMultiFileHashMapTable newTable, ExtendedMultiFileHashMapTableProvider newTableProvider) {
        currentTable = newTable;
        currentTableProvider = newTableProvider;
    }

    public ExtendedMultiFileHashMapTable getCurrentTable() {
        return currentTable;
    }

    public void changeCurrentTable(String name) {
        currentTable = currentTableProvider.getTable(name);
    }

    public ExtendedMultiFileHashMapTableProvider getCurrentTableProvider() {
        return currentTableProvider;
    }

    public boolean currentTableIsNull() {
        if (currentTable == null) {
            return true;
        }
        return false;
    }

    public String getFromCurrentTable(String key) {
        return currentTable.get(key);
    }

    public String putToCurrentTable(String key, String value) throws ParseException {
        return currentTable.put(key, value);
    }

    public String removeFromCurrentTable(String key) {
        return currentTable.remove(key);
    }

    public boolean removeTable(String name) throws IOException {
        try {
            currentTableProvider.removeTable(name);
        } catch (IllegalStateException ex) {
            return false;
        }
        return true;
    }

    public void checkDropTableUsing(String name) {
        if (currentTableProvider.getTable(name) == currentTable) {
            currentTable = null;
        }
    }

    public boolean createTable(String name) {
        return currentTableProvider.createTable(name) == null;
    }

    public boolean createStoreableTable(String name, String types) throws IOException {
        return false;
    }

    public boolean isTableExists(String name) {
        return currentTableProvider.getTable(name) != null;
    }

    public int getDiffCount() {
        return currentTable.getDiffCount();
    }

    public void writeTable() throws IOException {
        MultiFileHashMapUtils.writeTable(this);
    }

    public void readTable() throws IOException {
        MultiFileHashMapUtils.readTable(this);
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
