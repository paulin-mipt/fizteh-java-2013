package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMapBuilder;

public class DispatcherStoreableBuilder extends DispatcherMultiFileHashMapBuilder {
    @Override
    public Dispatcher construct() {
        return setPerformers(new DispatcherStoreable(forwarding, builder));
    }
}
