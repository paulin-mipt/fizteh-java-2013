package ru.fizteh.fivt.students.eltyshev.filemap.base;

import ru.fizteh.fivt.students.eltyshev.multifilemap.DatabaseFileDescriptor;

import java.util.HashMap;
import java.util.HashSet;

public class TransactionChanges<Key, Value> {
    HashMap<Key, Value> modifiedData;
    AbstractStorage<Key, Value> storage;
    int size;

    public HashSet<DatabaseFileDescriptor> getChangedFiles() {
        return changedFiles;
    }

    HashSet<DatabaseFileDescriptor> changedFiles = new HashSet<>();

    public TransactionChanges() {
        this.modifiedData = new HashMap<Key, Value>();
        this.size = 0;
    }

    public void addChange(Key key, Value value) {
        modifiedData.put(key, value);
    }

    public void setStorage(AbstractStorage storage) {
        this.storage = storage;
    }

    public int applyChanges() {
        int recordsChanged = 0;
        for (final Key key : modifiedData.keySet()) {
            Value newValue = modifiedData.get(key);
            if (!FileMapUtils.compareKeys(storage.oldData.get(key), newValue)) {
                if (newValue == null) {
                    storage.oldData.remove(key);
                } else {
                    storage.oldData.put(key, (Value) newValue);
                }
                changedFiles.add(storage.makeDescriptor(key));
                recordsChanged += 1;
            }
        }
        return recordsChanged;
    }

    public int countChanges() {
        int recordsChanged = 0;
        for (final Key key : modifiedData.keySet()) {
            Value newValue = modifiedData.get(key);
            if (!FileMapUtils.compareKeys(storage.oldData.get(key), newValue)) {
                recordsChanged += 1;
            }
        }
        return recordsChanged;
    }

    public int calcSize() {
        int result = 0;
        for (final Key key : modifiedData.keySet()) {
            Value newValue = modifiedData.get(key);
            Value oldValue = storage.oldData.get(key);
            if (newValue == null && oldValue != null) {
                result -= 1;
            }
            if (newValue != null && oldValue == null) {
                result += 1;
            }
        }
        return result;
    }

    public Value getValue(Key key) {
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key);
        }
        return storage.oldData.get(key);
    }

    public int getSize() {
        return storage.oldData.size() + calcSize();
    }

    public int getUncommittedChanges() {
        return countChanges();
    }

    public void clear() {
        modifiedData.clear();
        size = 0;
    }
}