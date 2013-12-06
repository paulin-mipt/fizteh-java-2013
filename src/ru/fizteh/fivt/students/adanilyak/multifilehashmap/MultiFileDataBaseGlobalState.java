package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.adanilyak.filemap.FileMapGlobalState;

import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:24
 */
public class MultiFileDataBaseGlobalState extends FileMapGlobalState {
    private TableProvider currentTableManager = null;
    public boolean autoCommitOnExit;

    public MultiFileDataBaseGlobalState() {
    }

    public MultiFileDataBaseGlobalState(TableProvider tableManager) {
        currentTableManager = tableManager;
        autoCommitOnExit = false;
    }

    /**
     * Operations with current table
     */

    public void setCurrentTable(String useTableAsCurrentName) {
        currentTable = currentTableManager.getTable(useTableAsCurrentName);
    }

    public int amountOfChanges() {
        return ((MultiFileTable) currentTable).getAmountOfChanges();
    }

    /**
     * Operations with current table manager
     */

    public boolean isTableExist(String tableName) {
        return (getMultiFileTable(tableName) != null);
    }

    public Table getMultiFileTable(String tableName) {
        return currentTableManager.getTable(tableName);
    }

    public void removeTable(String tableName) {
        if (currentTable != null) {
            if (currentTable.getName().equals(tableName)) {
                currentTable = null;
            }
        }
        currentTableManager.removeTable(tableName);
    }

    public void createTable(List<String> args) {
        String useTableName = args.get(1);
        if (getMultiFileTable(useTableName) != null) {
            System.err.println(useTableName + " exists");
        } else {
            currentTableManager.createTable(useTableName);
            System.out.println("created");
        }
    }
}
