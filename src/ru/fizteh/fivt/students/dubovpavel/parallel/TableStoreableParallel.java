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
        return new LockingWrapperQuiet<Integer>(lock.readLock()) {
            @Override
            protected Integer perform() {
                return localDict.size() + calculateAdditions(false) - calculateDeletions();
            }
        }.invoke();
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
    public Storeable put(final String key, final Storeable value) {
        return new LockingWrapperQuiet<Storeable>(lock.readLock()) {
            @Override
            protected Storeable perform() {
                checkPutInput(key, value);
                Storeable ret = getLocalValue(key);
                updates.get().put(key, value);
                removed.get().remove(key);
                return ret;
            }
        }.invoke();
    }

    @Override
    public Storeable get(final String key) {
        return new LockingWrapperQuiet<Storeable>(lock.readLock()) {
            @Override
            protected Storeable perform() {
                checkGetInput(key);
                return getLocalValue(key);
            }
        }.invoke();
    }

    @Override
    public Storeable remove(final String key) {
        return new LockingWrapperQuiet<Storeable>(lock.readLock()) {
            @Override
            protected Storeable perform() {
                checkRemoveInput(key);
                Storeable ret = getLocalValue(key);
                updates.get().remove(key);
                removed.get().add(key);
                return ret;
            }
        }.invoke();
    }

    @Override
    public int rollback() {
        return new LockingWrapperQuiet<Integer>(lock.readLock()) {
            @Override
            protected Integer perform() {
                int ret = countChanges();
                updates.get().clear();
                removed.get().clear();
                return ret;
            }
        }.invoke();
    }

    @Override
    public int commit() {
        return new LockingWrapperQuiet<Integer>(lock.writeLock()) {
            @Override
            protected Integer perform() {
                int ret = countChanges();
                localDict.putAll(updates.get());
                for (String key: removed.get()) {
                    localDict.remove(key);
                }
                TableStoreableParallel.super.commit();
                updates.get().clear();
                removed.get().clear();
                return ret;
            }
        }.invoke();
    }
}
