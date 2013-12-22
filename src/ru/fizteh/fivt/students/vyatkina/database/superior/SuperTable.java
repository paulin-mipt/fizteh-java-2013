package ru.fizteh.fivt.students.vyatkina.database.superior;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SuperTable<ValueType> {

    protected volatile Map<String, Diff<ValueType>> values = new HashMap<>();
    protected final String name;
    protected final ReadWriteLock tableKeeper = new ReentrantReadWriteLock(true);
    public SuperTable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ValueType get(String key) {

        TableChecker.keyValidCheck(key);
        Diff<ValueType> diff;
        try {
            tableKeeper.readLock().lock();
            diff = values.get(key);
            ValueType value = null;
            if (diff != null) {
                value = diff.getValue();
            }
            return value;
        }
        finally {
            tableKeeper.readLock().unlock();
        }
    }

    public ValueType put(String key, ValueType value) {

        TableChecker.keyValidCheck(key);
        TableChecker.valueIsNullCheck(value);

        Diff<ValueType> oldValue;
        ValueType oldStringValue;
        try {
            tableKeeper.writeLock().lock();
            oldValue = values.get(key);

            if (oldValue == null) {
                Diff<ValueType> newValue = new Diff(null, value);
                values.put(key, newValue);
                oldStringValue = null;

            } else {
                oldStringValue = oldValue.getValue();
                oldValue.setValue(value);
            }
        }
        finally {
            tableKeeper.writeLock().unlock();
        }

        return oldStringValue;
    }

    public ValueType remove(String key) {

        TableChecker.keyValidCheck(key);

        try {
            tableKeeper.readLock().lock();
            if (values.containsKey(key)) {
                return values.get(key).remove();
            } else {
                return null;
            }
        }
        finally {
            tableKeeper.readLock().unlock();
        }
    }

    public int commit() {
        int commited = 0;
        for (Diff<ValueType> value : values.values()) {
            if (value.commit()) {
                ++commited;
            }
        }
        return commited;
    }


    public int size() {
        int realSize = 0;
        try {
            tableKeeper.readLock().lock();
            for (Diff diff : values.values()) {
                if (!diff.isRemoved()) {
                    ++realSize;
                }
            }
        }
        finally {
            tableKeeper.readLock().unlock();
        }
        return realSize;
    }

    public int rollback() {
        int changes = 0;
        try {
            tableKeeper.readLock().lock();
            for (Diff diff : values.values()) {
                if (diff.rollback()) {
                    ++changes;
                }
            }
        }
        finally {
            tableKeeper.readLock().unlock();
        }
        return changes;
    }

    public Set<String> getKeys() {
        return values.keySet();
    }

    public Set<String> getKeysThatValuesHaveChanged() {
        Set<String> keysThatValuesHaveChanged = new HashSet<>();

        for (String key : values.keySet()) {
            if (values.get(key).isNeedToCommit()) {
                keysThatValuesHaveChanged.add(key);
            }
        }

        return keysThatValuesHaveChanged;
    }

    public void putValueFromDisk(String key, ValueType value) {
        try {
            tableKeeper.writeLock().lock();
            Diff<ValueType> valueFromDisk = new Diff(value, value);
            values.put(key, valueFromDisk);
        }
        finally {
            tableKeeper.writeLock().unlock();
        }
    }

    public void putValuesFromDisk(Map<String, ValueType> diskValues) {
        try {
            tableKeeper.writeLock().lock();
            for (Map.Entry<String, ValueType> entry : diskValues.entrySet()) {
                Diff<ValueType> valueFromDisk = new Diff(entry.getValue(), entry.getValue());
                values.put(entry.getKey(), valueFromDisk);
            }
        }
        finally {
            tableKeeper.writeLock().unlock();
        }
    }

    public int unsavedChanges() {
        int unsavedChanges = 0;
        try {
            tableKeeper.readLock().lock();
            for (String key : values.keySet()) {
                if (values.get(key).isNeedToCommit()) {
                    ++unsavedChanges;
                }
            }
        }
        finally {
            tableKeeper.readLock().unlock();
        }
        return unsavedChanges;
    }

    public Map<String, ValueType> entriesThatChanged() {
        Map<String, ValueType> result = new HashMap<>();
        for (Map.Entry<String, Diff<ValueType>> entry : values.entrySet()) {
            if (!entry.getValue().isRemoved()) {
                result.put(entry.getKey(), entry.getValue().getValue());
            }
        }
        return result;
    }


}
