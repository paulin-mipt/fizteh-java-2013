package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableStoreableParallel extends TableStoreable implements Table {
    private ThreadLocal<HashMap<String, Storeable>> updates;
    private ThreadLocal<HashSet<String>> removed;
    private ReentrantReadWriteLock lock;

    public TableStoreableParallel(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, types);
        lock = new ReentrantReadWriteLock(true);
        updates = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            protected HashMap<String, Storeable> initialValue() {
                return new HashMap<String, Storeable>();
            }
        };
        removed = new ThreadLocal<HashSet<String>>() {
            @Override
            protected HashSet<String> initialValue() {
                return new HashSet<String>();
            }
        };
    }

    // Other operations from interface Table are read-only

    private int calculateAdditions(boolean countModified) {
        int additions = 0;
        for (String key: updates.get().keySet()) {
            if (!localDict.containsKey(key)
                    || (countModified && !transformer.equal(updates.get().get(key), localDict.get(key)))) {
                ++additions;
            }
        }
        return additions;
    }

    private int calculateDeletions() {
        int deletions = 0;
        for (String key: removed.get()) {
            if (localDict.containsKey(key)) {
                ++deletions;
            }
        }
        return deletions;
    }

    private int countChanges() {
        return calculateAdditions(true) + calculateDeletions();
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return localDict.size() + calculateAdditions(false) - calculateDeletions();
        } finally {
            lock.readLock().unlock();
        }
    }

    private Storeable getLocalValue(String key) {
        if (updates.get().containsKey(key)) {
            return updates.get().get(key);
        } else if (removed.get().contains(key)) {
            return null;
        } else {
            return localDict.get(key);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        lock.readLock().lock();
        try {
            checkPutInput(key, value);
            Storeable ret = getLocalValue(key);
            updates.get().put(key, value);
            removed.get().remove(key);
            return ret;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable get(String key) {
        lock.readLock().lock();
        try {
            checkGetInput(key);
            return getLocalValue(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        lock.readLock().lock();
        try {
            checkRemoveInput(key);
            Storeable ret = getLocalValue(key);
            updates.get().remove(key);
            removed.get().add(key);
            return ret;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        lock.readLock().lock();
        try {
            int ret = countChanges();
            updates.get().clear();
            removed.get().clear();
            return ret;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        lock.writeLock().lock();
        try {
            int ret = countChanges();
            localDict.putAll(updates.get());
            for (String key: removed.get()) {
                localDict.remove(key);
            }
            super.commit();
            updates.get().clear();
            removed.get().clear();
            return ret;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
