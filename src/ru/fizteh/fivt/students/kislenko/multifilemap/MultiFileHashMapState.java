package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.filemap.CommandUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class MultiFileHashMapState extends MultiTableFatherState {
    private Path databasePath;
    private MyTable currentTable;
    private MyTableProvider tables;

    public MultiFileHashMapState(Path p) {
        databasePath = p;
        MyTableProviderFactory factory = new MyTableProviderFactory();
        tables = factory.create(p.toString());
        currentTable = null;
    }

    @Override
    public Path getPath() {
        return databasePath;
    }

    @Override
    public boolean alrightCreate(String tableName, AtomicReference<Exception> checkingException,
                                 AtomicReference<String> message) {
        return true;
    }

    @Override
    public void createTable(String[] tableParameters) {
        tables.createTable(databasePath.resolve(tableParameters[0]).toString());
    }

    @Override
    public void deleteTable(String tableName) {
        if (currentTable != null && databasePath.resolve(tableName).toString().equals(currentTable.getName())) {
            currentTable.clear();
            setCurrentTable(null);
        }
        tables.removeTable(databasePath.resolve(tableName).toString());
    }

    @Override
    public boolean needToChangeTable(String newTableName) {
        return currentTable == null || !currentTable.getName().equals(databasePath.resolve(newTableName).toString());
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    @Override
    public void dumpOldTable() throws IOException {
        if (currentTable != null) {
            Utils.dumpTable(currentTable);
            currentTable.clear();
        }
    }

    @Override
    public void changeTable(String tableName, AtomicReference<String> message) throws Exception {
        if (tables.getTable(databasePath.resolve(tableName).toString()) != null) {
            message.set("using " + tableName);
            currentTable = tables.getTable(databasePath.resolve(tableName).toString());
        } else {
            message.set(tableName + " not exists");
        }
    }

    @Override
    public int getTableChangeCount() {
        return 0;
    }

    @Override
    public boolean autoCommit() {
        return true;
    }

    @Override
    public int commitCurrentTable() {
        if (currentTable == null) {
            return 0;
        }
        return currentTable.commit();
    }

    public MyTable getCurrentTable() {
        return currentTable;
    }

    public Path getWorkingPath() {
        if (currentTable == null) {
            return databasePath;
        } else {
            return databasePath.resolve(currentTable.getName());
        }
    }

    public void setCurrentTable(String name) {
        if (name == null) {
            currentTable = null;
        } else {
            currentTable = tables.getTable(databasePath.resolve(name).toString());
        }
    }

    @Override
    public boolean alrightPutGetRemove(AtomicReference<Exception> checkingException, AtomicReference<String> message) {
        return CommandUtils.multiTablePutGetRemoveAlright(currentTable, checkingException, message);
    }

    @Override
    public String get(String key, AtomicReference<Exception> exception) {
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        try {
            Utils.loadFile(currentTable, twoLayeredKey);
        } catch (IOException e) {
            exception.set(e);
            return null;
        }
        return currentTable.get(key);
    }

    @Override
    public void put(String key, String value, AtomicReference<Exception> exception) {
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        try {
            Utils.loadFile(currentTable, twoLayeredKey);
        } catch (IOException e) {
            exception.set(e);
        }
        currentTable.put(key, value);
    }

    @Override
    public void remove(String key, AtomicReference<Exception> exception) {
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        try {
            Utils.loadFile(currentTable, twoLayeredKey);
        } catch (IOException e) {
            exception.set(e);
        }
        currentTable.remove(key);
    }
}
