package ru.fizteh.fivt.students.inaumov.filemap.base;

import ru.fizteh.fivt.storage.strings.Table;

public abstract class StringDatabaseTable extends AbstractDatabaseTable<String, String> implements Table {
    public StringDatabaseTable(String dir, String tableName) {
        super(dir, tableName);
    }

    @Override
    public String get(String key) {
        return tableGet(key);
    }

    @Override
    public String put(String key, String value) {
        return tablePut(key, value);
    }

    @Override
    public String remove(String key) {
        return tableRemove(key);
    }

    @Override
    public int commit() {
        return tableCommit();
    }

    @Override
    public int rollback() {
        return tableRollback();
    }

    @Override
    public int size() {
        return tableSize();
    }
}
