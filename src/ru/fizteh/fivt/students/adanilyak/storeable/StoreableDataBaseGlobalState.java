package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 20:33
 */
public class StoreableDataBaseGlobalState extends MultiFileDataBaseGlobalState {
    public Table currentTable = null;
    public TableProvider currentTableManager = null;
    public boolean autoCommitOnExit;

    public StoreableDataBaseGlobalState(TableProvider tableManager) {
        currentTableManager = tableManager;
        autoCommitOnExit = false;
    }

    /**
     * Operations with current table
     */

    @Override
    public String getCurrentTable() {
        if (currentTable == null) {
            return null;
        } else {
            return currentTable.getName();
        }
    }

    @Override
    public void setCurrentTable(String useTableAsCurrentName) {
        currentTable = currentTableManager.getTable(useTableAsCurrentName);
    }

    @Override
    public String put(String key, String value) {
        Storeable toPut = null;
        try {
            toPut = currentTableManager.deserialize(currentTable, value);
        } catch (ParseException exc) {
            System.err.println("storeable data base global state: making storeable problems");
        }
        Storeable resultStoreable = currentTable.put(key, toPut);
        if (resultStoreable == null) {
            return null;
        } else {
            return currentTableManager.serialize(currentTable, resultStoreable);
        }
    }

    @Override
    public String remove(String key) {
        Storeable toRemove = currentTable.remove(key);
        if (toRemove == null) {
            return null;
        } else {
            return currentTableManager.serialize(currentTable, toRemove);
        }
    }

    @Override
    public String get(String key) {
        Storeable toGet = currentTable.get(key);
        if (toGet == null) {
            return null;
        } else {
            return currentTableManager.serialize(currentTable, toGet);
        }
    }

    @Override
    public int commit() throws IOException {
        return currentTable.commit();
    }

    @Override
    public int rollback() {
        return currentTable.rollback();
    }

    @Override
    public int size() {
        return currentTable.size();
    }

    @Override
    public int amountOfChanges() {
        return ((StoreableTable) currentTable).getAmountOfChanges();
    }

    /**
     * Operations with current table provider
     */

    @Override
    public boolean isTableExist(String tableName) {
        return (getStoreableTable(tableName) != null);
    }

    public Table getStoreableTable(String tableName) {
        return currentTableManager.getTable(tableName);
    }

    @Override
    public void removeTable(String tableName) {
        if (currentTable != null) {
            if (currentTable.getName().equals(tableName)) {
                currentTable = null;
            }
        }
        try {
            currentTableManager.removeTable(tableName);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }
    }

    @Override
    public void createTable(List<String> args) {
        String useTableName = args.get(1);
        if (getStoreableTable(useTableName) != null) {
            System.err.println(useTableName + " exists");
        } else {
            try {
                List<Class<?>> columnTypes = WorkWithStoreableDataBase.createListOfTypes(args);
                currentTableManager.createTable(useTableName, columnTypes);
                System.out.println("created");
            } catch (IOException exc) {
                System.err.println(exc.getMessage());
            }
        }
    }
}
