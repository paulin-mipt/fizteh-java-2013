package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMap;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;

public class DispatcherStoreable extends DispatcherMultiFileHashMap {
    private PerformerAdapterStoreable adapter;

    public DispatcherStoreable(boolean forwarding, StorageBuilder builder) {
        super(forwarding, builder);
        adapter = new PerformerAdapterStoreable(storage);
    }

    @Override
    public DataBaseHandler<String, String> getDataBase() {
        return adapter.checked();
    }
}
