package ru.fizteh.fivt.students.vyatkina.database;

public class StringDatabaseAdapter implements DatabaseAdapter {

    private StringTableProvider tableProvider;
    private StringTable table;

    public StringDatabaseAdapter(StringTableProvider tableProvider, StringTable table) {
        this.tableProvider = tableProvider;
        this.table = table;
    }

    @Override
    public boolean tableIsSelected() {
        return !(table == null);
    }

    @Override
    public boolean createTable(String name) {
        return tableProvider.createTable(name) != null;
    }

    @Override
    public boolean createTable(String name, String structedSignature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean useTable(String name) {
        StringTable tableToUse = StringTable.class.cast(tableProvider.getTable(name));
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
        }
        catch (IllegalStateException e) {
            return false;
        }
        return true;
    }

    @Override
    public void saveChangesOnExit() {
        tableProvider.saveChangesOnExit();
    }

    @Override
    public String get(String key) {
        return table.get(key);
    }

    @Override
    public String put(String key, String value) {
        return table.put(key, value);
    }

    @Override
    public String remove(String key) {
        return table.remove(key);
    }

    @Override
    public int commit() {
        return table.commit();
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
        return table.unsavedChanges();
    }
}
