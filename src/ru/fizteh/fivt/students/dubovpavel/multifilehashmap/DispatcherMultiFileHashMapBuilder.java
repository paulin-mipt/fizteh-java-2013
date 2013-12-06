package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DispatcherFileMapBuilder;

public class DispatcherMultiFileHashMapBuilder extends DispatcherFileMapBuilder {
    protected StorageBuilder builder;

    public void setStorageBuilder(StorageBuilder storageBuilder) {
        builder = storageBuilder;
    }

    @Override
    public Dispatcher construct() {
        return setPerformers(new DispatcherMultiFileHashMap(forwarding, builder));
    }
}
