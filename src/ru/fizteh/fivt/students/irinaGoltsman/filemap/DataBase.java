package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.tools.ColumnTypes;
import ru.fizteh.fivt.students.irinaGoltsman.shell.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static TableProvider currentTableProvider = null;
    private static Table currentTable = null;
    private static List<Class<?>> currentColumnTypes = new ArrayList<>();

    public DataBase(TableProvider curTableProvider) {
        currentTableProvider = curTableProvider;
    }

    public static boolean checkTableChosen() {
        if (currentTable == null) {
            System.out.println("no table");
            return false;
        }
        return true;
    }

    public static Code use(String[] args) {
        if (currentTable != null) {
            int countOfChanges = ((DBTable) currentTable).countTheNumberOfChanges();
            if (countOfChanges != 0) {
                System.out.println(countOfChanges + " unsaved changes");
                return Code.OK;
            }
        }
        String inputTableName = args[1];
        Table tmpTable = currentTableProvider.getTable(inputTableName);
        if (tmpTable == null) {
            System.out.println(inputTableName + " not exists");
        } else {
            System.out.println("using " + inputTableName);
            currentTable = tmpTable;
        }
        return Code.OK;
    }

    public static Code get(String[] args) {
        if (!checkTableChosen()) {
            return Code.ERROR;
        }
        String key = args[1];
        Storeable value = currentTable.get(key);
        if (value != null) {
            System.out.println("found");
            String serializedValue = "";
            try {
                serializedValue = currentTableProvider.serialize(currentTable, value);
            } catch (ColumnFormatException e) {
                System.out.println(String.format("wrong type (%s)", e.getMessage()));
                return Code.OK;
            }
            System.out.println(serializedValue);
        } else {
            System.out.println("not found");
        }
        return Code.OK;
    }

    public static Code put(String[] args) {
        if (!checkTableChosen()) {
            return Code.ERROR;
        }
        String key = args[1];
        String value = args[2];
        try {
            Storeable deserializedValue = currentTableProvider.deserialize(currentTable, value);
            Storeable oldValue = currentTable.put(key, deserializedValue);
            if (oldValue != null) {
                System.out.println("overwrite");
                String serializedOldValue = currentTableProvider.serialize(currentTable, oldValue);
                System.out.println(serializedOldValue);
            } else {
                System.out.println("new");
            }
        } catch (ParseException e) {
            System.out.println(String.format("wrong type (%s)", e.getMessage()));
        }
        return Code.OK;
    }

    public static Code remove(String[] args) {
        if (!checkTableChosen()) {
            return Code.ERROR;
        }
        String key = args[1];
        Storeable removedValue = currentTable.remove(key);
        if (removedValue != null) {
            System.out.println("removed");
            return Code.OK;
        } else {
            System.out.println("not found");
            return Code.ERROR;
        }
    }

    private static int realCommit() throws IOException {
        if (currentTable == null) {
            return -1;
        }
        return currentTable.commit();
    }

    public static Code commit() {
        int numberOfRecordsWasChanged = 0;
        try {
            numberOfRecordsWasChanged = realCommit();
        } catch (IOException e) {
            return Code.ERROR;
        }
        if (numberOfRecordsWasChanged == -1) {
            return Code.ERROR;
        }
        System.out.println(numberOfRecordsWasChanged);
        return Code.OK;
    }

    public static Code createTable(String[] args) {
        String nameTable = args[1];
        String typesList = args[2];
        ColumnTypes ct = new ColumnTypes();
        List<Class<?>> types = new ArrayList<>();
        try {
            types = ct.parseColumnTypes(typesList);
        } catch (ParseException e) {
            System.out.println(String.format("wrong type (%s)", e.getMessage()));
            return Code.ERROR;
        }
        Table newTable = null;
        try {
            newTable = currentTableProvider.createTable(nameTable, types);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return Code.ERROR;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Code.ERROR;
        }
        if (newTable != null) {
            System.out.println("created");
            return Code.OK;
        } else {
            System.out.println(nameTable + " exists");
            return Code.ERROR;
        }
    }

    public static Code removeTable(String[] args) {
        String nameTable = args[1];
        if (currentTable != null && currentTable.getName().equals(nameTable)) {
            currentTable = null;
        }
        try {
            currentTableProvider.removeTable(nameTable);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return Code.ERROR;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Code.ERROR;
        }
        System.out.println("dropped");
        return Code.OK;
    }

    public static void closeDB() {
        /*
        try {
            realCommit();
        } catch (IOException e) {
            return Code.ERROR;
        }
        */
        currentTable = null;
    }

    public static Code size() {
        if (!checkTableChosen()) {
            return Code.ERROR;
        }
        System.out.println(currentTable.size());
        return Code.OK;
    }

    public static Code rollBack() {
        if (currentTable == null) {
            System.out.println(0);
            return Code.ERROR;
        }
        int countOfChangedKeys = currentTable.rollback();
        System.out.println(countOfChangedKeys);
        return Code.OK;
    }
}
