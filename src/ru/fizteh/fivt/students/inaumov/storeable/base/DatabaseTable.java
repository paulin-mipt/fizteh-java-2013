package ru.fizteh.fivt.students.inaumov.storeable.base;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.inaumov.filemap.base.AbstractDatabaseTable;
import ru.fizteh.fivt.students.inaumov.multifilemap.handlers.LoadHandler;
import ru.fizteh.fivt.students.inaumov.multifilemap.handlers.SaveHandler;
import ru.fizteh.fivt.students.inaumov.storeable.StoreableUtils;
import ru.fizteh.fivt.students.inaumov.storeable.builders.StoreableTableBuilder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import static ru.fizteh.fivt.students.inaumov.storeable.StoreableUtils.isStringIncorrect;

public class DatabaseTable extends AbstractDatabaseTable<String, Storeable> implements Table {
    private DatabaseTableProvider tableProvider;
    private List<Class<?>> columnTypes;

    public DatabaseTable(DatabaseTableProvider tableProvider, String databaseDirectory, String tableName,
                         List<Class<?>> columnTypes) {
        super(databaseDirectory, tableName);

        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("error: column types can't be null");
        }

        this.columnTypes = columnTypes;
        this.tableProvider = tableProvider;

        try {
            checkTableDir();
            loadTable();
        } catch (IOException e) {
            throw new IllegalArgumentException("error: incorrect file format");
        }
    }

    public DatabaseTable(DatabaseTable otherTable) {
        super(otherTable.getDir(), otherTable.rawGetTableName());
        this.columnTypes = otherTable.columnTypes;
        this.tableProvider = otherTable.tableProvider;
        this.keyValueHashMap = otherTable.keyValueHashMap;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key != null) {
            if (isStringIncorrect(key)) {
                throw new IllegalArgumentException("error: key can't contain whitespaces or be empty");
            }
        }
        if (value == null) {
            throw new IllegalArgumentException("error: value can't be null");
        }
        if (!checkAlienStoreable(value)) {
            throw new ColumnFormatException("error: alien storeable");
        }

        checkCorrectStoreable(value);

        return tablePut(key, value);
    }

    @Override
    public Storeable get(String key) {
        return tableGet(key);
    }

    @Override
    public Storeable remove(String key) {
        return tableRemove(key);
    }

    @Override
    public int size() {
        return tableSize();
    }

    @Override
    public int commit() throws IOException {
        return tableCommit();
    }

    @Override
    public int rollback() {
        return tableRollback();
    }

    @Override
    public int getColumnsCount() {
        tableState.checkAvailable();

        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) {
        tableState.checkAvailable();

        if (columnIndex < 0 || columnIndex > getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }

        return columnTypes.get(columnIndex);
    }

    @Override
    protected void loadTable() throws IOException {
        if (tableProvider == null) {
            return;
        }

        LoadHandler.loadTable(new StoreableTableBuilder(tableProvider, this));
    }

    @Override
    public void saveTable() throws IOException {
        SaveHandler.saveTable(new StoreableTableBuilder(tableProvider, this));
    }

    private void checkTableDir() throws IOException {
        File tableDirectory = new File(getDir(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
            writeSignatureFile();
        } else {
            File[] children = tableDirectory.listFiles();
            if (children == null || children.length == 0) {
                throw new IllegalArgumentException("error: table directory: "
                        + tableDirectory.getAbsolutePath() + " is empty");
            }
        }
    }

    private void writeSignatureFile() throws IOException {
        File tableDir = new File(getDir(), getName());
        File signatureFile = new File(tableDir, DatabaseTableProvider.SIGNATURE_FILE);
        StoreableUtils.writeSignature(signatureFile, columnTypes);
    }

    public boolean checkAlienStoreable(Storeable storeable) {
        for (int i = 0; i < getColumnsCount(); ++i) {
            try {
                Object obj = storeable.getColumnAt(i);
                if (obj == null) {
                    continue;
                }
                if (!obj.getClass().equals(getColumnType(i))) {
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        }

        try {
            storeable.getColumnAt(getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }

    public void checkCorrectStoreable(Storeable storeable) {
        for (int i = 0; i < getColumnsCount(); ++i) {
            try {
                StoreableUtils.isValueCorrect(storeable.getColumnAt(i), columnTypes.get(i));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public Set<String> rawGetKeys() {
        return keyValueHashMap.keySet();
    }

    public String getTableDir() {
        File tableDir = new File(getDir(), getName());
        return tableDir.getAbsolutePath();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getTableDir() + "]";
    }
}
