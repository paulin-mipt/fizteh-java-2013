package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.utils.StoreableTableFileManager;

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

public class StoreableTable implements Table, AutoCloseable {

    private boolean isClosed;
    private int commitedSize;
    private final boolean autoCommit;   
    private final Lock getCommitLock;
    private final String name;

    private final List<Class<?>> valueTypes;
    private final Map<String, Storeable> commited;
    private final StoreableTableProvider provider;

    private final ThreadLocal<HashMap<String, Storeable>> changed = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };

    private final ThreadLocal<HashSet<String>> deleted = new ThreadLocal<HashSet<String>>() {
        @Override
        protected HashSet<String> initialValue() {
            return new HashSet<String>();
        }
    };

    public StoreableTable(StoreableTableProvider provider, 
        String name, List<Class<?>> valueTypes) 
    throws ValidityCheckFailedException, IOException {
        
        this(provider, name, false, valueTypes);
    }

    public StoreableTable(StoreableTableProvider provider, 
        String name, boolean autoCommit, List<Class<?>> valueTypes) 
    throws ValidityCheckFailedException, IOException {

        this.name = name;
        this.provider = provider;
        commited = new HashMap<String, Storeable>();

        commitedSize = 0;
        this.autoCommit = autoCommit;
        //fair queue
        getCommitLock = new ReentrantLock(true);

        if (valueTypes == null) {
            throw new IllegalArgumentException("Value types not specified");
        }

        this.valueTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(valueTypes));
        isClosed = false;

        setInitialSize(provider, name);
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkClosed();

        try {
            ValidityChecker.checkTableKey(key);
            ValidityChecker.checkTableValue(value);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        try {
            ValidityChecker.checkValueFormat(this, value);
        } catch (ValidityCheckFailedException ex) {
            throw new ColumnFormatException(ex.getMessage());
        }

        Storeable returnValue = get(key);

        //putting the same value as in the last commited version
        //effectively discards any changes made to it
        //anyway, local changes should be applied no matter what
        changed.get().put(key, value);

        //the value put back is no longer deleted
        deleted.get().remove(key);

        if (autoCommit) {
            pushChanges();
        }
        return returnValue;
    }

    public Storeable get(String key) {
        checkClosed();

        try {
            ValidityChecker.checkTableKey(key);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        if (deleted.get().contains(key)) {
            return null;
        }

        if (changed.get().get(key) != null) {
            return changed.get().get(key);
        }

        getCommitLock.lock();

        try {
            if (getCommited(key) != null) {
                return getCommited(key);
            }
        } finally {
            getCommitLock.unlock();
        }

        //redundant but still
        return null;
    }

    public Storeable remove(String key) {
        checkClosed();

        try {
            ValidityChecker.checkTableKey(key);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        Storeable returnValue = get(key);
        Storeable commitedValue = null;

        getCommitLock.lock();

        try {
            commitedValue = getCommited(key);
        } finally {
            getCommitLock.unlock();
        }

        //if present, the key should be deleted from a commited version of a table
        if (commitedValue != null) {
            deleted.get().add(key);
        }
        //it is deleted from local changes regardless
        changed.get().remove(key);

        if (autoCommit) {
            pushChanges();
        }
        return returnValue;
    }

    public String getName() {
        checkClosed();
        return name;
    }

    public int commit() throws IOException {
        checkClosed();

        int diffNum;

        getCommitLock.lock();

        try {

            diffNum = getDiffCount();

            //NB: first calculate size, then push changes
            //System.out.println(commitedSize);
            int newSize = size();
            storeOnCommit();
            pushChanges();
            
            commitedSize = newSize;

        } catch (ValidityCheckFailedException ex) {
            throw new RuntimeException("Validity check failed: " + ex.getMessage());
        } finally {
            getCommitLock.unlock();
        }

        changed.get().clear();
        deleted.get().clear();

        return diffNum;
    }

    public int rollback() {
        checkClosed();

        int diffNum;

        getCommitLock.lock();
        try {
            diffNum = getDiffCount();
        } finally {
            getCommitLock.unlock();
        }

        changed.get().clear();
        deleted.get().clear();

        return diffNum;
    }

    //should be locked from the outside, unless made sure that the object is thread-unique
    //synchronized, just to be safe

    public synchronized void pushChanges() {
        for (Map.Entry<String, Storeable> entry : changed.get().entrySet()) {
            commited.put(entry.getKey(), entry.getValue());
        }

        for (String entry : deleted.get()) {
            commited.remove(entry);
        }
    }

    //should be locked from the outside, unless made sure that the object is thread-unique 
    public int getDiffCount() {

        int diffCount = 0;

        for (Map.Entry<String, Storeable> entry : changed.get().entrySet()) {

            if ((getCommited(entry.getKey()) == null) 
                || (!isValueEqual(entry.getValue(), getCommited(entry.getKey())))) {

                ++diffCount;
            }
        }

        for (String entry : deleted.get()) {
            if (getCommited(entry) != null) {
                ++diffCount;
            }
        }
        return diffCount;
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
        
        getCommitLock.lock();
        
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
            getCommitLock.unlock();
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
