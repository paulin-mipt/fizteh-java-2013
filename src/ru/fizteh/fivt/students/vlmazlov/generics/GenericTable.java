package ru.fizteh.fivt.students.vlmazlov.generics;

import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.AbstractMap;

public abstract class GenericTable<V> implements Iterable<Map.Entry<String, V>>, Cloneable {

    private class GenericTableIterator implements Iterator<Map.Entry<String, V>> {
        private final Iterator<Map.Entry<String, WeakReference<V>>> commitedIterator;

        private GenericTableIterator() {
            commitedIterator = commited.entrySet().iterator();
        }

        public boolean hasNext() {
            return commitedIterator.hasNext();
        }

        public Map.Entry<String, V> next() {
           String nextKey = commitedIterator.next().getKey();

           return new AbstractMap.SimpleEntry(nextKey, get(nextKey));
        }

        public void remove() {
            //Not necessary
        }
    }

    private Map<String, WeakReference<V>> commited;
    protected GenericTableProvider<V, ? extends GenericTable<V>> provider;

    private final ThreadLocal<HashMap<String, V>> changed = new ThreadLocal<HashMap<String, V>>() {
        protected HashMap<String, V> initialValue() {
            return new HashMap<String, V>();
        }
    };

    private final ThreadLocal<HashSet<String>> deleted = new ThreadLocal<HashSet<String>>() {
        protected HashSet<String> initialValue() {
            return new HashSet<String>();
        }
    };

    private final String name;
    protected final boolean autoCommit;
    private int size;
    private ReadWriteLock getCommitLock;

    public GenericTable(GenericTableProvider<V, ? extends GenericTable<V>> provider, String name) {
        this(provider, name, true);
    }

    public GenericTable(GenericTableProvider<V, ? extends GenericTable<V>> provider, String name, boolean autoCommit) {
        this.name = name;
        this.provider = provider;
        commited = new HashMap<String, WeakReference<V>>();

        size = 0;
        this.autoCommit = autoCommit;
        //fair queue
        getCommitLock = new ReentrantReadWriteLock(true);
    }

    public Iterator iterator() {
        return new GenericTableIterator();
    }

    protected abstract void loadFileForKey(String key);

    private V getCommited(String key) {
        if (commited.get(key) == null) {
            return null;
        }

        if (commited.get(key).get() == null) {
            loadFileForKey(key);
        }
        
        return commited.get(key).get();
    }

    public V put(String key, V value) {
        try {
            ValidityChecker.checkTableKey(key);
            ValidityChecker.checkTableValue(value);
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        V returnValue = get(key);

        if (returnValue == null) {
            ++size;
        }

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

        getCommitLock.readLock().lock();

        try {
            if (getCommited(key) != null) {
                return getCommited(key);
            }
        } finally {
            getCommitLock.readLock().unlock();
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

        getCommitLock.readLock().lock();

        try {
            commitedValue = getCommited(key);
        } finally {
            getCommitLock.readLock().unlock();
        }

        if (returnValue != null) {
            --size;
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

    public int size() {
        return size;
    }

    public String getName() {
        return name;
    }

    protected abstract void storeOnCommit() throws IOException, ValidityCheckFailedException;

    public int commit() throws IOException {
        int diffNum;

        getCommitLock.writeLock().lock();

        try {
            diffNum = getDiffCount();

            pushChanges();
            storeOnCommit();

        } catch (ValidityCheckFailedException ex) {
            throw new RuntimeException("Validity check failed: " + ex.getMessage());
        } finally {

            getCommitLock.writeLock().unlock();
        }

        changed.get().clear();
        deleted.get().clear();

        return diffNum;
    }

    public int rollback() {
        int diffNum;

        getCommitLock.readLock().lock();
        try {
            diffNum = getDiffCount();
        } finally {
            getCommitLock.readLock().unlock();
        }

        changed.get().clear();
        deleted.get().clear();

        return diffNum;
    }

    //should be locked from the outside, unless made sure that the object is thread-unique
    //synchronized, just to be safe

    public synchronized void pushChanges() {
        for (Map.Entry<String, V> entry : changed.get().entrySet()) {
            commited.put(entry.getKey(), new WeakReference<V>(entry.getValue()));
        }

        for (String entry : deleted.get()) {
            commited.remove(entry);
        }
    }

    //should be locked from the outside, unless made sure that the object is thread-unique 
    public int getDiffCount() {

        int diffCount = 0;

        for (Map.Entry<String, V> entry : changed.get().entrySet()) {

            if ((commited.get(entry.getKey()) == null) 
                || (!isValueEqual(entry.getValue(), getCommited(entry.getKey())))) {

                ++diffCount;
            }
        }

        for (String entry : deleted.get()) {
            if (commited.get(entry) != null) {
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
