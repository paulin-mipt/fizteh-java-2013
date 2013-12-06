package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vlmazlov.generics.GenericTable;
import ru.fizteh.fivt.students.vlmazlov.utils.ProviderWriter;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoreableTable extends GenericTable<Storeable> implements Table, Cloneable, AutoCloseable {

    private StoreableTableProvider specificProvider;
    private boolean isClosed;
    private final List<Class<?>> valueTypes;

    public StoreableTable(StoreableTableProvider provider, String name, List<Class<?>> valueTypes) {
        super(provider, name);
        if (valueTypes == null) {
            throw new IllegalArgumentException("Value types not specified");
        }

        specificProvider = provider;
        ///questionable
        this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
        isClosed = false;
    }

    public StoreableTable(StoreableTableProvider provider, String name, boolean autoCommit, List<Class<?>> valueTypes) {
        super(provider, name, autoCommit);
        specificProvider = provider;
        this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
        isClosed = false;
    }

    @Override
    public String getName() {
        checkClosed();
        return super.getName();
    }

    @Override
    public Storeable get(String key) {
        checkClosed();
        return super.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkClosed();

        try {
            ValidityChecker.checkValueFormat(this, value);
        } catch (ValidityCheckFailedException ex) {
            throw new ColumnFormatException(ex.getMessage());
        }

        return super.put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        checkClosed();
        return super.remove(key);
    }

    @Override
    public int size() {
        checkClosed();
        return super.size();
    }

    @Override
    public int commit() throws IOException {
        checkClosed();
        return super.commit();
    }

    @Override
    public int rollback() {
        checkClosed();
        return super.rollback();
    }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return valueTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkClosed();
        return valueTypes.get(columnIndex);
    }

    @Override
    public StoreableTable clone() {
        checkClosed();
        return new StoreableTable(specificProvider, getName(), autoCommit, valueTypes);
    }

    @Override
    protected boolean isValueEqual(Storeable first, Storeable second) {
        checkClosed();
        return specificProvider.serialize(this, first).equals(specificProvider.serialize(this, second));
    }

    @Override
    public void checkRoot(File root) throws ValidityCheckFailedException {
        checkClosed();
        ValidityChecker.checkMultiStoreableTableRoot(root);
    }

    @Override
    protected void storeOnCommit() throws IOException, ValidityCheckFailedException {
        checkClosed();
        ProviderWriter.writeMultiTable(this, new File(specificProvider.getRoot(), getName()), specificProvider);
    }

    public void close() {
        if (isClosed) {
            return;
        }

        specificProvider.closeTable(getName());
        rollback();
        isClosed = true;
    }

    public void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("trying to operate on a closed table");
        }
    }

    public String toString() {
        checkClosed();
        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("[");
        builder.append(new File(provider.getRoot(), getName()).getPath());
        builder.append("]");

        return builder.toString();
    }
}
