package ru.fizteh.fivt.students.kislenko.junit;


import ru.fizteh.fivt.students.kislenko.filemap.CommandUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class JUnitState extends TransactionalFatherState {
    private Path databasePath;
    private MyTable currentTable;
    private MyTableProvider tables;

    public JUnitState(Path p) {
        databasePath = p;
        MyTableProviderFactory factory = new MyTableProviderFactory();
        tables = factory.create(p.toString());
    }

    public Path getPath() {
        return databasePath;
    }

    public Path getWorkingPath() {
        if (currentTable != null) {
            return databasePath.resolve(currentTable.getName());
        } else {
            return databasePath;
        }
    }

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
        return true;
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
            Utils.readTable(currentTable);
        } else {
            message.set(tableName + " not exists");
        }
    }

    @Override
    public int getTableChangeCount() {
        if (currentTable == null) {
            return 0;
        }
        return currentTable.getChangeCount();
    }

    public MyTable getCurrentTable() {
        return currentTable;
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
        return currentTable.get(key);
    }

    @Override
    public void put(String key, String value, AtomicReference<Exception> exception) {
        currentTable.put(key, value);
    }

    @Override
    public void remove(String key, AtomicReference<Exception> exception) {
        currentTable.remove(key);
    }

    @Override
    public boolean alrightCreate(String empty, AtomicReference<Exception> useless1, AtomicReference<String> useless2) {
        return true;
    }

    @Override
    public void createTable(String[] tableParameters) {
        tables.createTable(databasePath.resolve(tableParameters[0]).toString());
    }

    @Override
    public boolean hasCurrentTable() {
        return currentTable != null;
    }

    @Override
    public void dumpCurrentTable() throws IOException {
        if (currentTable == null) {
            return;
        }
        Utils.dumpTable(currentTable);
    }

    @Override
    public int commitCurrentTable() {
        if (currentTable == null) {
            return 0;
        }
        return currentTable.commit();
    }

    @Override
    public int rollbackCurrentTable() {
        if (currentTable == null) {
            return 0;
        }
        return currentTable.rollback();
    }

    @Override
    public int getCurrentTableSize() {
        if (currentTable == null) {
            return 0;
        }
        return currentTable.size();
    }
}
