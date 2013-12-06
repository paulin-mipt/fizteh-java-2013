package ru.fizteh.fivt.students.vyatkina.database.superior;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Diff<ValueType> {

    private AtomicReference<ValueType> committedValue = new AtomicReference<>();
    private ThreadLocal<Boolean> isChanged = new ThreadLocal<>();
    private ThreadLocal<ValueType> value = new ThreadLocal<>();
    private ReadWriteLock commitedValueKeeper = new ReentrantReadWriteLock();

    public Diff(ValueType committedValue, ValueType value) {
        this.committedValue.set(committedValue);
        if (!areTheSame(committedValue, value)) {
            this.value.set(value);
            isChanged.set(true);
        }
    }

    private boolean areTheSame(ValueType a, ValueType b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    public ValueType getValue() {
        if (isChanged.get() == null) {
            try {
                commitedValueKeeper.readLock().lock();
                return committedValue.get();
            }
            finally {
                commitedValueKeeper.readLock().unlock();
            }
        } else {
            return value.get();
        }
    }

    public void setValue(ValueType value) {
        try {
            commitedValueKeeper.readLock().lock();
            if (areTheSame(committedValue.get(), value)) {
                isChanged.set(null);
            } else {
                isChanged.set(true);
                this.value.set(value);
            }
        }
        finally {
            commitedValueKeeper.readLock().unlock();
        }
    }

    private void refreshIsChanged() {
        try {
            commitedValueKeeper.readLock().lock();
            if ((isChanged.get() != null)
                    && (areTheSame(committedValue.get(), value.get()))) {
                isChanged.set(null);
            }
        }
        finally {
            commitedValueKeeper.readLock().unlock();
        }
    }

    public boolean isNeedToCommit() {
        refreshIsChanged();
        return isChanged.get() != null;
    }

    public boolean commit() {
        try {
            commitedValueKeeper.writeLock().lock();
            if (isNeedToCommit()) {
                committedValue.set(value.get());
                return true;
            } else {
                return false;
            }
        }
        finally {
            commitedValueKeeper.writeLock().unlock();
        }
    }

    public boolean rollback() {
        if (isNeedToCommit()) {
            isChanged.set(null);
            return true;
        } else {
            return false;
        }
    }

    public ValueType remove() {
        ValueType oldValue = getValue();
        isChanged.set(true);
        value.set(null);
        refreshIsChanged();
        return oldValue;
    }

    public boolean isRemoved() {
        return getValue() == null;
    }

}
