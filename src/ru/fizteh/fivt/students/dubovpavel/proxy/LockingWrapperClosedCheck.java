package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.students.dubovpavel.parallel.LockingWrapper;

import java.util.concurrent.locks.Lock;

public abstract class LockingWrapperClosedCheck
        <S extends ClosedCheckable, T, E1 extends Throwable, E2 extends Throwable>
        extends LockingWrapper<T, E1, E2> {
    private S structure;
    public LockingWrapperClosedCheck(S structure, Lock lock) {
        super(lock);
        this.structure = structure;
    }

    @Override
    protected void check() {
        if (structure.closed()) {
            throw new IllegalStateException(String.format("%s was closed", structure.getClass().getName()));
        }
    }
}
