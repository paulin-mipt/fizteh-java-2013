package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapState implements State<MultiFileHashMapTable> {
    private File dbDir;
    private MultiFileHashMapTable curTable;
    private MultiFileHashMapTableProvider tables;

    public MultiFileHashMapState(File dir) throws IOException {
        dbDir = dir;

        MultiFileHashMapTableProviderFactory tpf = new MultiFileHashMapTableProviderFactory();

        tables = tpf.create(dir.toString());
    }

    public File getCurDir() {
        return dbDir;
    }

    public MultiFileHashMapTable getCurTable() {
        return curTable;
    }

    public void createTable(String curTableName) throws IOException {
        tables.createTable(dbDir.toPath().resolve(curTableName).toString());
    }

    public void setCurTable(String curTableName) {
        if (curTableName == null) {
            curTable = null;
        } else {
            this.curTable = tables.getTable(dbDir.toPath().resolve(curTableName).toString());
        }
    }

    public void removeTable(String curTableName) throws IOException {
        tables.removeTable(curTableName);
    }

    public void saveTable(MultiFileHashMapTable table) throws IOException {
        AbstractMultiFileHashMap.saveTable(table);
    }

    public void readTableOff(MultiFileHashMapTable table) throws IOException {
        AbstractMultiFileHashMap.readTableOff(table);
    }

    public String get(String key) {
        return curTable.get(key);
    }

    public String remove(String key) {
        return curTable.remove(key);
    }

    public String put(String key, String value) {
        return curTable.put(key, value);
    }

    public int commit() {
        return curTable.commit();
    }

    public int rollback() {
        return curTable.rollback();
    }

    public int size() {
        return curTable.size();
    }

    public int getDiffSize() {
        return curTable.getDiffSize();
    }

    public File getCurTableDir() {
        return curTable.getCurTableDir();
    }

    public void clearContentAndDiff() {
        curTable.clearContentAndDiff();
    }

    public boolean usingTables() {
        return true;
    }
}
