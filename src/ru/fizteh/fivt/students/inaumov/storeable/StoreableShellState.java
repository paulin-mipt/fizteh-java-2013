package ru.fizteh.fivt.students.inaumov.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;

import java.io.IOException;
import java.text.ParseException;

public class StoreableShellState implements MultiFileMapShellState<Table, String, Storeable> {
    Table table;
    TableProvider tableProvider;

    public StoreableShellState(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }

    @Override
    public Table createTable(String arguments) {
        TableInfo newTableInfo = StoreableUtils.parseCreateCommand(arguments);
        Table newTable;

        try {
            newTable = tableProvider.createTable(newTableInfo.getTableName(), newTableInfo.getColumnTypes());
        } catch (IOException e) {
            newTable = null;
        }

        return newTable;
    }

    @Override
    public Table useTable(String tableName) {
        Table newTable = tableProvider.getTable(tableName);
        if (newTable != null) {
            table = newTable;
        }

        return newTable;
    }

    @Override
    public void dropTable(String tableName) throws IOException {
        tableProvider.removeTable(tableName);

        table = null;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public String getCurrentTableName() {
        return table.getName();
    }

    @Override
    public int commit() {
        try {
            return table.commit();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public Storeable put(String key, Storeable value) {
        return table.put(key, value);
    }

    @Override
    public Storeable get(String key) {
        return table.get(key);
    }

    @Override
    public Storeable remove(String key) {
        return table.remove(key);
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public String keyToString(String key) {
        return key;
    }

    @Override
    public String parseKey(String key) {
        return key;
    }

    @Override
    public String valueToString(Storeable storeable) {
        String str = tableProvider.serialize(table, storeable);

        return str;
    }

    @Override
    public Storeable parseValue(String value) {
        try {
            return tableProvider.deserialize(table, value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("error: incorrect value format");
        }
    }

    @Override
    public String[] parsePutCommand(String argumentsLine) {
        if (argumentsLine == null || argumentsLine.trim().isEmpty()) {
            return new String[0];
        }

        argumentsLine = argumentsLine.trim();

        int spaceFirstEntry = argumentsLine.indexOf(' ');

        if (spaceFirstEntry == -1) {
            return new String[]{argumentsLine};
        }

        String keyString = argumentsLine.substring(0, spaceFirstEntry).trim();
        String valueString = argumentsLine.substring(spaceFirstEntry).trim();

        return new String[]{keyString, valueString};
    }

    @Override
    public String[] parseCreateCommand(String argumentsLine) {
        if (argumentsLine == null || argumentsLine.trim().isEmpty()) {
            return new String[0];
        }

        return new String[]{argumentsLine};
    }
}
