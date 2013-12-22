package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.parallel.LockingWrapperQuiet;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableProviderStoreableParallelFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderSessionalFactory extends TableProviderStoreableParallelFactory
        implements AutoCloseable, ClosedCheckable {
    private HashSet<TableProviderSessional> created = new HashSet<>();
    private ReentrantReadWriteLock closingLock = new ReentrantReadWriteLock(true);
    private boolean closed = false;

    public boolean closed() {
        return closed;
    }

    public TableProvider create(final String path) throws IOException {
        return new LockingWrapperClosedCheck<TableProviderSessionalFactory, TableProvider,
                IOException, RuntimeException>(this, closingLock.readLock()) {
            @Override
            protected TableProvider perform() throws IOException {
                checkPath(path);
                StorageBuilder storageBuilder = new StorageBuilder();
                storageBuilder.setPath(false, path);
                TableSessionalBuilder dataBaseBuilder = new TableSessionalBuilder();
                storageBuilder.setDataBaseBuilder(dataBaseBuilder);
                TableProviderSessional provider =
                        new TableProviderSessional<TableSessional>(storageBuilder.construct(), dataBaseBuilder);
                created.add(provider);
                return provider;
            }
        }.invoke();
    }

    public void close() {
        new LockingWrapperQuiet<Void>(closingLock.writeLock()) {
            @Override
            protected Void perform() {
                if (!closed) {
                    for (TableProviderSessional provider: created) {
                        provider.close();
                    }
                    closed = true;
                }
                return null;
            }
        }.invoke();
    }
}
