package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class StoreableState implements State<StoreableTable> {
    private File dbDir;
    private StoreableTable curTable;
    private StoreableTableProvider tables;

    public StoreableState(File dir) throws IOException {
        dbDir = dir;

        StoreableTableProviderFactory tpf = new StoreableTableProviderFactory();

        tables = tpf.create(dir.toString());
    }

    public File getCurDir() {
        return dbDir;
    }

    public StoreableTable getCurTable() {
        return curTable;
    }

    public void createTable(String curTableName) throws IOException, ClassNotFoundException {
        String s = dbDir.toPath().resolve(curTableName).toString();

        tables.createTable(s, AbstractStoreable.readTypes(s));
    }

    public void setCurTable(String curTableName) {
        if (curTableName == null) {
            curTable = null;
        } else {
            curTable = tables.getTable(dbDir.toPath().resolve(curTableName).toString());
        }
    }

    public void removeTable(String curTableName) throws IOException {
        tables.removeTable(curTableName);
    }

    public void saveTable(StoreableTable table) throws IOException {
        AbstractStoreable.saveTable(table);
    }

    public void readTableOff(StoreableTable table) throws IOException, ParseException {
        AbstractStoreable.readTableOff(table);
    }

    public String get(String key) {
        if (curTable.get(key) == null) {
            return null;
        }

        return tables.serialize(curTable, curTable.get(key));
    }

    public String remove(String key) {
        if (curTable.get(key) == null) {
            return null;
        }

        return tables.serialize(curTable, curTable.remove(key));
    }

    public String put(String key, String value) throws ParseException {
        Storeable newValue = tables.deserialize(curTable, value);
        Storeable putEntry = curTable.put(key, newValue);

        if (putEntry == null) {
            return null;
        }

        return tables.serialize(curTable, putEntry);
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
