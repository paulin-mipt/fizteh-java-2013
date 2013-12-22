package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableProviderStoreable;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderStoreableParallel<DB extends FileRepresentativeDataBase<Storeable>
        & Table>
        extends TableProviderStoreable<DB> implements TableProvider {
    protected ReentrantReadWriteLock lock;

    public TableProviderStoreableParallel(Storage storage, TableStoreableBuilder builder) {
        super(storage, builder);
        lock = new ReentrantReadWriteLock(true);
    }

    @Override
    public Table createTable(final String name, final List<Class<?>> columnTypes) throws IOException {
        return new LockingWrapper<Table, IOException, RuntimeException>(lock.writeLock()) {
            @Override
            protected Table perform() throws IOException {
                return TableProviderStoreableParallel.super.createTable(name, columnTypes);
            }
        }.invoke();
    }

    @Override
    public DB getTable(final String name) {
        return new LockingWrapperQuiet<DB>(lock.readLock()) {
            @Override
            protected DB perform() {
                return TableProviderStoreableParallel.super.getTable(name);
            }
        }.invoke();
    }

    @Override
    public void removeTable(final String name) throws IOException {
        new LockingWrapper<Void, IOException, RuntimeException>(lock.writeLock()) {
            @Override
            protected Void perform() throws IOException {
                TableProviderStoreableParallel.super.removeTable(name);
                return null;
            }
        }.invoke();
    }
}
