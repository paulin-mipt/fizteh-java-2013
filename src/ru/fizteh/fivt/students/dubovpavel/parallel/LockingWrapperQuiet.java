package ru.fizteh.fivt.students.dubovpavel.parallel;

import java.util.concurrent.locks.Lock;

public abstract class LockingWrapperQuiet<T> extends LockingWrapper<T, RuntimeException, RuntimeException> {
    public LockingWrapperQuiet(Lock lock) {
        super(lock);
    }
}
