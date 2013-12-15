package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.parallel.LockingWrapperQuiet;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableProviderStoreableParallel;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderSessional<DB extends FileRepresentativeDataBase<Storeable>
        & Table & AutoCloseable & ClosedCheckable>
        extends TableProviderStoreableParallel<DB> implements AutoCloseable, ClosedCheckable {
    private boolean closed;
    private ReentrantReadWriteLock closingLock;

    @Override
    public String toString() {
        return ProxyUtils.generateRepr(this, storage.getPath());
    }

    public TableProviderSessional(Storage storage, TableStoreableBuilder builder) {
        super(storage, builder);
        closed = false;
        closingLock = new ReentrantReadWriteLock(true);
    }

    public boolean closed() {
        return closed;
    }

    public void close() {
        new LockingWrapperQuiet<Void>(closingLock.writeLock()) {
            @Override
            protected Void perform() {
                if (!closed) {
                    for (Iterator<DB> i = storage.getDBIterator(); i.hasNext(); ) {
                        try {
                            i.next().close();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    closed = true;
                }
                return null;
            }
        }.invoke();
    }

    @Override
    public Table createTable(final String name, final List<Class<?>> columnTypes) throws IOException {
        return new LockingWrapperClosedCheck<TableProviderSessional, Table,
                IOException, RuntimeException>(this, closingLock.readLock()) {
            @Override
            protected Table perform() throws IOException {
                return TableProviderSessional.super.createTable(name, columnTypes);
            }
        }.invoke();
    }

    @Override
    public DB getTable(final String name) {
        return new LockingWrapperQuietClosedCheck<TableProviderSessional, DB>(this, closingLock.readLock()) {
            @Override
            protected DB perform() {
                return new LockingWrapperQuiet<DB>(lock.readLock()) {
                    @Override
                    protected DB perform() {
                        DB table = TableProviderSessional.super.getTable(name);
                        if (table != null && table.closed()) {
                            return storage.reOpenDataBase(table.getPath());
                        } else {
                            return table;
                        }
                    }
                }.invoke();
            }
        }.invoke();
    }

    @Override
    public void removeTable(final String name) throws IOException {
        new LockingWrapperClosedCheck<TableProviderSessional, Void,
                IOException, RuntimeException>(this, closingLock.readLock()) {
            @Override
            protected Void perform() throws IOException {
                TableProviderSessional.super.removeTable(name);
                return null;
            }
        }.invoke();
    }

    @Override
    public Storeable deserialize(final Table table, final String value) throws ParseException {
        return new LockingWrapperClosedCheck<TableProviderSessional, Storeable,
                ParseException, RuntimeException>(this, closingLock.readLock()) {
            @Override
            protected Storeable perform() throws ParseException {
                return TableProviderSessional.super.deserialize(table, value);
            }
        }.invoke();
    }

    @Override
    public String serialize(final Table table, final Storeable value) throws ColumnFormatException {
        return new LockingWrapperClosedCheck<TableProviderSessional, String,
                ColumnFormatException, RuntimeException>(this, closingLock.readLock()) {
            @Override
            protected String perform() throws ColumnFormatException {
                return TableProviderSessional.super.serialize(table, value);
            }
        }.invoke();
    }

    @Override
    public Storeable createFor(final Table table) {
        return new LockingWrapperQuietClosedCheck<TableProviderSessional, Storeable>(this, closingLock.readLock()) {
            @Override
            protected Storeable perform() {
                return TableProviderSessional.super.createFor(table);
            }
        }.invoke();
    }

    @Override
    public Storeable createFor(final Table table, final List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        return new LockingWrapperClosedCheck<TableProviderSessional, Storeable,
                ColumnFormatException, IndexOutOfBoundsException>(this, closingLock.readLock()) {
            @Override
            protected Storeable perform() throws ColumnFormatException, IndexOutOfBoundsException {
                return TableProviderSessional.super.createFor(table, values);
            }
        }.invoke();
    }
}
