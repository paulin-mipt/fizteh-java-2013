package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.utils.StoreableTableFileManager;
import ru.fizteh.fivt.students.vlmazlov.generics.GenericTable;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.text.ParseException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StoreableTable extends GenericTable<Storeable> implements Table, AutoCloseable {

    private boolean isClosed;

    private final List<Class<?>> valueTypes;
    private final StoreableTableProvider provider;

    
    private final Lock getCommitLock;


    public StoreableTable(StoreableTableProvider provider, 
        String name, List<Class<?>> valueTypes) 
    throws ValidityCheckFailedException, IOException {
        
        this(provider, name, false, valueTypes);
    }

    public StoreableTable(StoreableTableProvider provider, 
        String name, boolean autoCommit, List<Class<?>> valueTypes) 
    throws ValidityCheckFailedException, IOException {
    	super(name, autoCommit);

        this.provider = provider;

        //fair queue
        getCommitLock = new ReentrantLock(true);

        if (valueTypes == null) {
            throw new IllegalArgumentException("Value types not specified");
        }

        this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
        isClosed = false;

        setInitialSize(provider, name);
    }

    @Override
    protected void readLock(){
    	getCommitLock.lock();
    }
    
    @Override
    protected void readUnLock() {
    	getCommitLock.unlock();
    }
    
    @Override
    protected void writeLock() {
    	getCommitLock.lock();
    }
    
    @Override
    protected void writeUnLock() {
    	getCommitLock.unlock();
    }

    private void setInitialSize(StoreableTableProvider provider, String name)
    throws ValidityCheckFailedException, IOException {
        getCommitLock.lock();

        try {
            commitedSize = StoreableTableFileManager.getTableSize(name, provider, this);
        } finally {
            getCommitLock.unlock();
        }
    }

    //MUST be under lock
    private void loadKey(String key) 
    throws IOException, ValidityCheckFailedException, ParseException {
        checkClosed();

        Map<String, Storeable> fileData = StoreableTableFileManager.readFileForKey(key, this, provider);
        Storeable value = fileData.get(key);
                
        commited.putAll(fileData);

        //presence of the key in question, if it is the case, must be ensured
        if (value != null) {
            commited.put(key, value);
        }
    }

    //MUST be under lock
    protected Storeable getCommited(String key) {
        checkClosed();

        if (!commited.containsKey(key)) {
            try {
                loadKey(key);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to load key from file: " + ex.getMessage());
            }
        }

        return commited.get(key);
    }

    public int getColumnsCount() {
        checkClosed();
        return valueTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkClosed();
        return valueTypes.get(columnIndex);
    }

    protected boolean isValueEqual(Storeable first, Storeable second) {
        checkClosed();
        return provider.serialize(this, first).equals(provider.serialize(this, second));
    }

    public void checkRoot(File root) throws ValidityCheckFailedException {
        checkClosed();
        ValidityChecker.checkMultiStoreableTableRoot(root);
    }

    public int size() {
        checkClosed();
        
        readLock();
        
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
            readUnLock();
        }

        return size;
    }

    protected void storeOnCommit() throws IOException, ValidityCheckFailedException {
        checkClosed();

        StoreableTableFileManager.writeSize(this, provider);
        StoreableTableFileManager.writeSignature(this, provider);
        StoreableTableFileManager.modifyMultipleFiles(changed.get(), deleted.get(), this, provider);
    }

    public void close() {
        if (isClosed) {
            return;
        }

        provider.closeTable(getName());
        rollback();
        isClosed = true;
    }

    public void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("trying to operate on a closed table");
        }
    }

    @Override
    public String toString() {
        checkClosed();
        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("[");
        builder.append(new File(provider.getRoot(), getName()).getPath());
        builder.append("]");

        return builder.toString();
    }

    public StoreableTable clone() {
    	throw new RuntimeException("Cloning not supported!");
    }
}
