package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.students.kislenko.filemap.CommandUtils;
import ru.fizteh.fivt.students.kislenko.junit.TransactionalFatherState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicReference;

public class StoreableState extends TransactionalFatherState {
    private Path databasePath;
    private MyTable currentTable;
    private MyTableProvider tables;

    public StoreableState(Path p) throws IOException {
        databasePath = p;
        MyTableProviderFactory factory = new MyTableProviderFactory();
        tables = factory.create(p.toString());
    }

    public Path getPath() {
        return databasePath;
    }

    @Override
    public void deleteTable(String tableName) throws Exception {
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

    public void createTable(String tableName) throws Exception {
        String temp = databasePath.resolve(tableName).toString();
        tables.createTable(temp, Utils.readColumnTypes(temp));
    }

    public MyTable getCurrentTable() {
        return currentTable;
    }

    public Path getWorkingPath() {
        if (currentTable != null) {
            return databasePath.resolve(currentTable.getName());
        } else {
            return databasePath;
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
    public boolean alrightCreate(String tableName, AtomicReference<Exception> checkingException,
                                 AtomicReference<String> message) {
        if (tableName.contains(".")) {
            message.set("Dots in table name.");
            checkingException.set(new RuntimeException("Dots in table name."));
            return false;
        }
        return true;
    }

    @Override
    public void createTable(String[] tableParameters) throws Exception {
        String[] types = new String[tableParameters.length - 1];
        System.arraycopy(tableParameters, 1, types, 0, tableParameters.length - 1);
        if (types.length == 0) {
            System.out.println("wrong type (signature expected)");
            throw new IllegalArgumentException("Signature expected.");
        }
        types[0] = types[0].substring(1);
        types[types.length - 1] = types[types.length - 1].substring(0, types[types.length - 1].length() - 1);
        if (types[0].isEmpty()) {
            System.out.println("wrong type (signature is empty)");
            throw new IllegalArgumentException("Empty signature.");
        }
        try {
            Utils.writeColumnTypes(databasePath.resolve(tableParameters[0]).toString(), types);
        } catch (Exception e) {
            System.out.println("wrong type (signature is so bad that I can't create new table with it)");
            new File(databasePath.resolve(tableParameters[0]).toString(), "signature.tsv").delete();
            throw e;
        }
        tables.createTable(databasePath.resolve(tableParameters[0]).toString(),
                Utils.readColumnTypes(databasePath.resolve(tableParameters[0]).toString()));
    }

    @Override
    public String get(String key, AtomicReference<Exception> exception) {
        if (currentTable.get(key) == null) {
            return null;
        }
        return currentTable.getProvider().serialize(currentTable, currentTable.get(key));
    }

    @Override
    public void put(String key, String value, AtomicReference<Exception> exception) {
        try {
            currentTable.put(key, tables.deserialize(currentTable, value));
        } catch (ParseException e) {
            exception.set(e);
        }
    }

    @Override
    public void remove(String key, AtomicReference<Exception> exception) {
        currentTable.remove(key);
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
    public int commitCurrentTable() throws IOException {
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
