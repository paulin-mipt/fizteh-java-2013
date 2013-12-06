package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container.TableContainer;
import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTable;
import ru.fizteh.fivt.students.asaitgalin.storable.values.TableValuePackerStorable;
import ru.fizteh.fivt.students.asaitgalin.storable.values.TableValueUnpackerStorable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MultiFileTable implements ExtendedTable {
    // Underlying container
    private TableContainer<Storeable> container;

    private String name;
    private List<Class<?>> columnTypes;

    public MultiFileTable(File tableDir, String name, TableProvider provider) {
        this.name = name;
        MultiFileTableSignatureWorker worker = new MultiFileTableSignatureWorker(tableDir);
        columnTypes = worker.readColumnTypes();
        this.container = new TableContainer<>(tableDir, new TableValuePackerStorable(this, provider),
                new TableValueUnpackerStorable(this, provider));
    }

    public MultiFileTable(File tableDir, String name, TableProvider provider, List<Class<?>> columnTypes) {
        this.name = name;
        this.columnTypes = columnTypes;
        this.container = new TableContainer<>(tableDir, new TableValuePackerStorable(this, provider),
                new TableValueUnpackerStorable(this, provider));
        MultiFileTableSignatureWorker worker = new MultiFileTableSignatureWorker(tableDir);
        worker.writeColumnTypes(columnTypes);
    }

    @Override
    public int getChangesCount() {
        return container.containerGetChangesCount();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: key is null");
        }
        return container.containerGetValue(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: key or value is null");
        }
        if (key.matches("\\s*") || key.split("\\s+").length != 1) {
            throw new IllegalArgumentException("put: key or value is empty");
        }
        checkValue(value);
        return container.containerPutValue(key, value);
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key is null");
        }
        return container.containerRemoveValue(key);
    }

    @Override
    public int size() {
        return container.containerGetSize();
    }

    @Override
    public int commit() throws IOException {
        return container.containerCommit();
    }

    @Override
    public int rollback() {
        return container.containerRollback();
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException(String.format("table, getColumnType: index %d out of bounds", columnIndex));
        }
        return columnTypes.get(columnIndex);
    }

    public void load() throws IOException {
        container.containerLoad();
    }

    private void tryToGetValue(Storeable st, int index, Class<?> type) throws ColumnFormatException {
        switch (type.getSimpleName()) {
            case "Integer":
                st.getIntAt(index);
                break;
            case "Long":
                st.getLongAt(index);
                break;
            case "Byte":
                st.getByteAt(index);
                break;
            case "Float":
                st.getFloatAt(index);
                break;
            case "Double":
                st.getDoubleAt(index);
                break;
            case "Boolean":
                st.getBooleanAt(index);
                break;
            case "String":
                st.getStringAt(index);
                break;
            default:
                throw new ColumnFormatException("table: wrong storable columns");
        }
    }

    private void checkValue(Storeable st) throws ColumnFormatException {
        int counter = 0;
        try {
            for (; counter < columnTypes.size(); ++counter) {
                tryToGetValue(st, counter, columnTypes.get(counter));
            }
            try {
                st.getColumnAt(counter);
                throw new ColumnFormatException("table: wrong storable columns");
            } catch (IndexOutOfBoundsException e) {
                // Check if st has more columns. If we caught this, it means that it has the same columns count.
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("table: wrong storable columns");
        }
    }

}
