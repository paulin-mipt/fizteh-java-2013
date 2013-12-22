package ru.fizteh.fivt.students.dubovpavel.parallel;

import java.util.concurrent.locks.Lock;

public abstract class LockingWrapper<T, E1 extends Throwable, E2 extends Throwable> {
    Lock lock;
    public LockingWrapper(Lock lock) {
        this.lock = lock;
    }

    protected abstract T perform() throws E1, E2;
    protected void check() {}

    public T invoke() throws E1, E2 {
        try {
            lock.lock();
            check();
            return perform();
        } finally {
            lock.unlock();
        }
    }
}
