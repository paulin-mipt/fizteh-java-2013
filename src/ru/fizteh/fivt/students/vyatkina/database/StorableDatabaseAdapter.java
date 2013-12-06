package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class StorableDatabaseAdapter implements DatabaseAdapter {

    private StorableTableProvider tableProvider;
    private StorableTable table;

    public StorableDatabaseAdapter(StorableTableProvider tableProvider, StorableTable table) {
        this.tableProvider = tableProvider;
        this.table = table;
    }

    @Override
    public boolean tableIsSelected() {
        return !(table == null);
    }

    @Override
    public boolean createTable(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createTable(String name, String structuredSignature) {
        List<Class<?>> columnTypes = tableProvider.parseStructedSignature(structuredSignature);
        try {
            if (tableProvider.createTable(name, columnTypes) != null) {
                return true;
            } else {
                return false;
            }
        }
        catch (IOException e) {
            throw new WrappedIOException(e.getMessage());
        }
    }

    @Override
    public boolean useTable(String name) {
        StorableTable tableToUse = StorableTable.class.cast(tableProvider.getTable(name));
        if (tableToUse != null) {
            this.table = tableToUse;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dropTable(String name) {
        try {
            tableProvider.removeTable(name);
            if (table != null && table.getName().equals(name)) {
                table = null;
            }
        }
        catch (IllegalStateException e) {
            return false;
        }
        catch (IOException e) {
            throw new WrappedIOException(e.getMessage());
        }
        return true;
    }

    @Override
    public void saveChangesOnExit() {
        tableProvider.saveChangesOnExit();
    }

    @Override
    public String get(String key) {
        Storeable value = table.get(key);
        if (value == null) {
            return null;
        } else {
            try {
                return tableProvider.serialize(table, value);
            }
            catch (ColumnFormatException e) {
                throw new WrappedIOException(e.getMessage());
            }
        }
    }

    @Override
    public String put(String key, String value) {
        Storeable structuredValue;
        try {
            structuredValue = tableProvider.deserialize(table, value);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        Storeable oldStructuredValue = table.put(key, structuredValue);
        if (oldStructuredValue != null) {
            try {
                return tableProvider.serialize(table, oldStructuredValue);
            }
            catch (ColumnFormatException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else {
            return null;
        }
    }

    @Override
    public String remove(String key) {
        Storeable oldStructuredValue = table.remove(key);
        if (oldStructuredValue != null) {
            try {
                return tableProvider.serialize(table, oldStructuredValue);
            }
            catch (ColumnFormatException e) {
                throw new WrappedIOException(e.getMessage());
            }
        } else {
            return null;
        }
    }

    @Override
    public int commit() {
        int savedChanges;
        try {
            savedChanges = table.commit();
        }
        catch (IOException e) {
            throw new WrappedIOException(e.getMessage());
        }
        return savedChanges;
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public int unsavedChanges() {
        if (table == null) {
            return 0;
        }
        return table.unsavedChanges();
    }
}
