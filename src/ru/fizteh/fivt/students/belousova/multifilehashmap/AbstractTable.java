package ru.fizteh.fivt.students.belousova.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractTable<KeyType, ValueType> {
    protected Map<KeyType, ValueType> dataBase = new HashMap<KeyType, ValueType>();
    protected ThreadLocal<Map<KeyType, ValueType>> addedKeys;// = new HashMap<KeyType, ValueType>();
    protected ThreadLocal<Set<KeyType>> deletedKeys;// = new HashSet<KeyType>();

    protected final Lock tableTransactionsLock = new ReentrantLock(true);

    protected File dataDirectory = null;

    public String getName() {
        return dataDirectory.getName();
    }

    public ValueType get(KeyType key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }

        if (addedKeys.get().containsKey(key)) {
            return addedKeys.get().get(key);
        }
        if (deletedKeys.get().contains(key)) {
            return null;
        }

        tableTransactionsLock.lock();
        try {
            return dataBase.get(key);
        } finally {
            tableTransactionsLock.unlock();
        }
    }

    public ValueType put(KeyType key, ValueType value) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty value");
        }
        tableTransactionsLock.lock();
        try {
            if (dataBase.containsKey(key) && !deletedKeys.get().contains(key)) {
                deletedKeys.get().add(key);
                ValueType oldValue = dataBase.get(key);
                addedKeys.get().put(key, value);
                return oldValue;
            }
        } finally {
            tableTransactionsLock.unlock();
        }
        return addedKeys.get().put(key, value);
    }

    public ValueType remove(KeyType key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        tableTransactionsLock.lock();
        try {
            if (dataBase.containsKey(key) && !deletedKeys.get().contains(key)) {
                deletedKeys.get().add(key);
                return dataBase.get(key);
            }
        } finally {
            tableTransactionsLock.unlock();
        }
        return addedKeys.get().remove(key);
    }

    public int size() {
        tableTransactionsLock.lock();
        try {
            for (KeyType key : deletedKeys.get()) {
                if (!dataBase.containsKey(key)) {
                    deletedKeys.get().remove(key);
                }
            }
            Set<KeyType> addedKeysSet = addedKeys.get().keySet();
            Set<KeyType> addedKeysForDeletion = new HashSet<>();
            for (KeyType key : addedKeysSet) {
                if (dataBase.containsKey(key)) {
                    if (dataBase.get(key).equals(addedKeys.get().get(key))) {
                        addedKeysForDeletion.add(key);
                        if (deletedKeys.get().contains(key)) {
                            deletedKeys.get().remove(key);
                        }
                    } else {
                        if (!deletedKeys.get().contains(key)) {
                            deletedKeys.get().add(key);
                        }
                    }
                }
            }
            addedKeys.get().keySet().removeAll(addedKeysForDeletion);
            return dataBase.size() + addedKeys.get().size() - deletedKeys.get().size();
        } finally {
            tableTransactionsLock.unlock();
        }
    }

    protected int countChanges() {
        int changesCounter = addedKeys.get().size() + deletedKeys.get().size();

        for (KeyType key : addedKeys.get().keySet()) {
            if (deletedKeys.get().contains(key)) {
                changesCounter--;
                if (dataBase.get(key).equals(addedKeys.get().get(key))) {
                    changesCounter--;
                }
            }
        }

        return changesCounter;
    }

    public abstract int commit() throws IOException;

    public int rollback() {
        tableTransactionsLock.lock();
        int counter;
        try {
            counter = countChanges();
        } finally {
            tableTransactionsLock.unlock();
        }
        deletedKeys.get().clear();
        addedKeys.get().clear();
        return counter;
    }

    public int getChangesCount() {
        tableTransactionsLock.lock();
        try {
            return countChanges();
        } finally {
            tableTransactionsLock.unlock();
        }
    }
}
