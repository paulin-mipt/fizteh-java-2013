package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vlmazlov.generics.GenericTable;
import ru.fizteh.fivt.students.vlmazlov.utils.ProviderWriter;
import ru.fizteh.fivt.students.vlmazlov.utils.ProviderReader;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.utils.StoreableTableFileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.text.ParseException;

public class StoreableTable extends GenericTable<Storeable> implements Table, Cloneable, AutoCloseable {

    private StoreableTableProvider specificProvider;
    private boolean isClosed;
    private final List<Class<?>> valueTypes;

    public StoreableTable(StoreableTableProvider provider, 
        String name, List<Class<?>> valueTypes) 
    throws ValidityCheckFailedException, IOException {
        
        super(provider, name, false);

        setInitialSize(provider, name);

        if (valueTypes == null) {
            throw new IllegalArgumentException("Value types not specified");
        }

        specificProvider = provider;
        ///questionable
        this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
        isClosed = false;
    }

    public StoreableTable(StoreableTableProvider provider, 
        String name, boolean autoCommit, List<Class<?>> valueTypes) 
    throws ValidityCheckFailedException, IOException {
        
        super(provider, name, autoCommit);

        setInitialSize(provider, name);

        if (valueTypes == null) {
            throw new IllegalArgumentException("Value types not specified");
        }

        specificProvider = provider;
        this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
        isClosed = false;
    }

    private void setInitialSize(StoreableTableProvider provider, String name)
    throws ValidityCheckFailedException, IOException {
        getCommitLock.writeLock().lock();

        try {
            commitedSize = StoreableTableFileManager.getTableSize(name, provider);
        } finally {
            getCommitLock.writeLock().unlock();
        }
    }

    //MUST be under lock
    private void loadKey(String key) 
    throws IOException, ValidityCheckFailedException, ParseException {
        checkClosed();

        Storeable value = StoreableTableFileManager.readSingleKey(key, this, specificProvider);

        if (value != null) { 
            if (!Thread.currentThread().holdsLock(getCommitLock.writeLock())) {

                getCommitLock.readLock().unlock();
                getCommitLock.writeLock().lock();

                try {
                
                    commited.put(key, value);
                
                } finally    {
                    getCommitLock.writeLock().unlock();
                    getCommitLock.readLock().lock();
                }

            } else {
                commited.put(key, value);
            }
        }
    }

    //MUST be under lock
    @Override
    protected Storeable getCommited(String key) {
        checkClosed();

        if (!commited.containsKey(key)) {
            try {
                loadKey(key);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to load key from file: " + ex.getMessage());
            }
        }

        //readlock reaquired in loadKey()
        return commited.get(key);
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

    //the table is now strongly tied to the disk, therefore, the method is not applicable
    @Override
    public StoreableTable clone() {
        checkClosed();
        throw new RuntimeException("Cloning not supported in the current version");
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
    public int size() {
        checkClosed();
        
        getCommitLock.readLock().lock();
        
        int size = commitedSize;

        try {

            for (Map.Entry<String, Storeable> entry : changed.get().entrySet()) {
                if (getCommited(entry.getKey()) == null) {
                    ++size;
                }
            }

            for (String entry : deleted.get()) {
                if (getCommited(entry) != null) {
                    --size;
                }
            }
        } finally {
            getCommitLock.readLock().unlock();
        }

        return size;
    }

    @Override
    protected void storeOnCommit() throws IOException, ValidityCheckFailedException {
        checkClosed();

        StoreableTableFileManager.writeSize(this, specificProvider);
        StoreableTableFileManager.writeSignature(this, specificProvider);
        StoreableTableFileManager.modifyMultipleFiles(changed.get(), deleted.get(), this, specificProvider);
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
