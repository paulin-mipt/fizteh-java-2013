package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.parallel.LockingWrapperQuiet;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableStoreableParallel;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableSessional extends TableStoreableParallel implements AutoCloseable, ClosedCheckable {
    private boolean closed;
    private ReentrantReadWriteLock closingLock;

    public TableSessional(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, types);
        closed = false;
        closingLock = new ReentrantReadWriteLock(true);
    }

    @Override
    public String getName() {
        return new LockingWrapperQuietClosedCheck<TableSessional, String>(this, closingLock.readLock()) {
            @Override
            protected String perform() {
                return TableSessional.super.getName();
            }
        }.invoke();
    }

    @Override
    public int getColumnsCount() {
        return new LockingWrapperQuietClosedCheck<TableSessional, Integer>(this, closingLock.readLock()) {
            @Override
            protected Integer perform() {
                return TableSessional.super.getColumnsCount();
            }
        }.invoke();
    }

    @Override
    public Class<?> getColumnType(final int columnIndex) throws IndexOutOfBoundsException {
        return new LockingWrapperClosedCheck<TableSessional, Class<?>,
                IndexOutOfBoundsException, RuntimeException>(this, closingLock.readLock()) {
            @Override
            protected Class<?> perform() throws IndexOutOfBoundsException {
                return TableSessional.super.getColumnType(columnIndex);
            }
        }.invoke();
    }

    public boolean closed() {
        return closed;
    }

    public void close() throws Exception {
        new LockingWrapperQuiet<Void>(closingLock.writeLock()) {
            @Override
            protected Void perform() {
                if (!closed) {
                    TableSessional.super.rollback();
                    closed = true;
                }
                return null;
            }
        }.invoke();
    }

    @Override
    public String toString() {
        return ProxyUtils.generateRepr(this, root.getAbsolutePath());
    }

    // getName is not overridden on purpose

    @Override
    public int size() {
        return new LockingWrapperQuietClosedCheck<TableSessional, Integer>(this, closingLock.readLock()) {
            @Override
            public Integer perform() {
                return TableSessional.super.size();
            }
        }.invoke();
    }

    @Override
    public Storeable put(final String key, final Storeable value) {
        return new LockingWrapperQuietClosedCheck<TableSessional, Storeable>(this, closingLock.readLock()) {
            @Override
            protected Storeable perform() {
                return TableSessional.super.put(key, value);
            }
        }.invoke();
    }

    @Override
    public Storeable get(final String key) {
        return new LockingWrapperQuietClosedCheck<TableSessional, Storeable>(this, closingLock.readLock()) {
            @Override
            protected Storeable perform() {
                return TableSessional.super.get(key);
            }
        }.invoke();
    }

    @Override
    public Storeable remove(final String key) {
        return new LockingWrapperQuietClosedCheck<TableSessional, Storeable>(this, closingLock.readLock()) {
            @Override
            protected Storeable perform() {
                return TableSessional.super.remove(key);
            }
        }.invoke();
    }

    @Override
    public int rollback() {
        return new LockingWrapperQuietClosedCheck<TableSessional, Integer>(this, closingLock.readLock()) {
            @Override
            protected Integer perform() {
                return TableSessional.super.rollback();
            }
        }.invoke();
    }

    @Override
    public int commit() {
        return new LockingWrapperQuietClosedCheck<TableSessional, Integer>(this, closingLock.readLock()) {
            @Override
            protected Integer perform() {
                return TableSessional.super.commit();
            }
        }.invoke();
    }
}
