package ru.fizteh.fivt.students.dubovpavel.proxy;

import java.util.concurrent.locks.Lock;

public abstract class LockingWrapperQuietClosedCheck<S extends ClosedCheckable, T>
        extends LockingWrapperClosedCheck<S, T, RuntimeException, RuntimeException> {
    public LockingWrapperQuietClosedCheck(S structure, Lock lock) {
        super(structure, lock);
    }
}
