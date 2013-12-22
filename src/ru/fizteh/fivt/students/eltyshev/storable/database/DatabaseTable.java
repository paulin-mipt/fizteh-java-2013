package ru.fizteh.fivt.students.eltyshev.storable.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.eltyshev.filemap.base.AbstractStorage;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DatabaseFileDescriptor;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DistributedLoader;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DistributedSaver;
import ru.fizteh.fivt.students.eltyshev.multifilemap.MultifileMapUtils;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

public class DatabaseTable extends AbstractStorage<String, Storeable> implements Table {
    DatabaseTableProvider provider;

    private List<Class<?>> columnTypes;

    public DatabaseTable(DatabaseTableProvider provider, String databaseDirectory, String tableName, List<Class<?>> columnTypes) {
        super(databaseDirectory, tableName);
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("wrong type (column types cannot be null)");
        }
        this.columnTypes = columnTypes;
        this.provider = provider;

        try {
            checkTableDirectory();
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("wrong type (invalid file format)");
        }
    }

    public DatabaseTable(DatabaseTable other) {
        super(other.getDatabaseDirectory(), other.rawGetName());
        this.columnTypes = other.columnTypes;
        this.provider = other.provider;
        this.oldData = other.oldData;
    }

    @Override
    public Storeable get(String key) {
        return storageGet(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key != null) {
            if (StoreableUtils.checkStringCorrect(key)) {
                throw new IllegalArgumentException("key cannot be empty");
            }
        }

        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (!checkAlienStoreable(value)) {
            throw new ColumnFormatException("alien storeable");
        }
        checkCorrectStoreable(value);

        return storagePut(key, value);
    }

    @Override
    public Storeable remove(String key) {
        return storageRemove(key);
    }

    @Override
    public int size() {
        return storageSize();
    }

    @Override
    public int commit() throws IOException {
        return storageCommit();
    }

    @Override
    public int rollback() {
        return storageRollback();
    }

    @Override
    public int getColumnsCount() {
        state.checkOperationsAllowed();

        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        state.checkOperationsAllowed();

        if (columnIndex < 0 || columnIndex > getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    @Override
    protected void load() throws IOException {
        if (provider == null) {
            return;
        }
        DistributedLoader.load(new StoreableTableBuilder(provider, this));
    }

    @Override
    protected void save() throws IOException {
        DistributedSaver.save(new StoreableTableBuilder(provider, this), getChangedFiles());
    }

    @Override
    protected DatabaseFileDescriptor makeDescriptor(String key) {
        return MultifileMapUtils.makeDescriptor(key);
    }

    private void checkTableDirectory() throws IOException {
        File tableDirectory = new File(getDatabaseDirectory(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
            writeSignatureFile();
        } else {
            File[] children = tableDirectory.listFiles();
            writeSignatureFile();
            if (children == null || children.length == 0) {
                throw new IllegalArgumentException(String.format("wrong type (table directory: %s is empty)", tableDirectory.getAbsolutePath()));
            }
        }
    }

    private void writeSignatureFile() throws IOException {
        File tableDirectory = new File(getDatabaseDirectory(), getName());
        File signatureFile = new File(tableDirectory, DatabaseTableProvider.SIGNATURE_FILE);
        StoreableUtils.writeSignature(signatureFile, columnTypes);
    }

    public boolean checkAlienStoreable(Storeable storeable) {
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                Object o = storeable.getColumnAt(index);
                if (o == null) {
                    continue;
                }
                if (!o.getClass().equals(getColumnType(index))) {
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
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                StoreableUtils.checkValue(storeable.getColumnAt(index), columnTypes.get(index));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    Set<String> rawGetKeys() {
        return oldData.keySet();
    }

    public String getTableDirectory() {
        File tableDirectory = new File(getDatabaseDirectory(), getName());
        return tableDirectory.getAbsolutePath();
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), getTableDirectory());
    }
}
