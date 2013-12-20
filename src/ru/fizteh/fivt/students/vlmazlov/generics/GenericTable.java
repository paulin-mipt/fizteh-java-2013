package ru.fizteh.fivt.students.vlmazlov.generics;

import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public abstract class GenericTable<V> implements Iterable<Map.Entry<String, V>>, Cloneable {

    protected Map<String, V> commited;

    protected final ThreadLocal<HashMap<String, V>> changed = new ThreadLocal<HashMap<String, V>>() {
        protected HashMap<String, V> initialValue() {
            return new HashMap<String, V>();
        }
    };

    protected final ThreadLocal<HashSet<String>> deleted = new ThreadLocal<HashSet<String>>() {
        protected HashSet<String> initialValue() {
            return new HashSet<String>();
        }
    };

    protected int commitedSize;
    protected final boolean autoCommit;   
    private final String name;

    public GenericTable(String name) {
        this(name, true);
    }

    protected GenericTable(String name, boolean autoCommit) {

            this.name = name;
            commited = new HashMap<String, V>();

            commitedSize = 0;
            this.autoCommit = autoCommit;
    }

    public Iterator iterator() {
        return commited.entrySet().iterator();
    }

    //queue must be fair

    protected abstract void readLock();
    protected abstract void readUnLock();
    protected abstract void writeLock();
    protected abstract void writeUnLock();

    protected abstract V getCommited(String key);

    public V put(String key, V value) {
        try {
            ValidityChecker.checkTableKey(key);
            ValidityChecker.checkTableValue(value);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        V returnValue = get(key);

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

    public V get(String key) {
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

        readLock();

        try {
            if (getCommited(key) != null) {
                return getCommited(key);
            }
        } finally {
            readUnLock();
        }

        //redundant but still
        return null;
    }

    public V remove(String key) {
        try {
            ValidityChecker.checkTableKey(key);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        V returnValue = get(key);
        V commitedValue = null;

        readLock();

        try {
            commitedValue = getCommited(key);
        } finally {
            readUnLock();
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

    public abstract int size();

    public String getName() {
        return name;
    }

    protected abstract void storeOnCommit() throws IOException, ValidityCheckFailedException;

    public int commit() throws IOException {
        int diffNum;

        writeLock();

        try {

            readLock();

            try {
                diffNum = getDiffCount();
            } finally {
                readUnLock();
            }

            //NB: first calculate size, then push changes
            //System.out.println(commitedSize);
            int newSize = size();
            
            storeOnCommit();
            pushChanges();
            
            commitedSize = newSize;

        } catch (ValidityCheckFailedException ex) {
            throw new RuntimeException("Validity check failed: " + ex.getMessage());
        } finally {

            writeUnLock();
        }

        changed.get().clear();
        deleted.get().clear();

        return diffNum;
    }

    public int rollback() {
        int diffNum;

        readLock();
        try {
            diffNum = getDiffCount();
        } finally {
            readUnLock();
        }

        changed.get().clear();
        deleted.get().clear();

        return diffNum;
    }

    //should be locked from the outside, unless made sure that the object is thread-unique
    //synchronized, just to be safe

    public synchronized void pushChanges() {
        for (Map.Entry<String, V> entry : changed.get().entrySet()) {
            commited.put(entry.getKey(), entry.getValue());
        }

        for (String entry : deleted.get()) {
            commited.remove(entry);
        }
    }

    //should be locked from the outside, unless made sure that the object is thread-unique 
    public int getDiffCount() {

        int diffCount = 0;

        for (Map.Entry<String, V> entry : changed.get().entrySet()) {

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

    public abstract GenericTable<V> clone();

    //both != null
    protected boolean isValueEqual(V first, V second) {
        return first.equals(second);
    }

    public abstract void checkRoot(File root) throws ValidityCheckFailedException;
}
