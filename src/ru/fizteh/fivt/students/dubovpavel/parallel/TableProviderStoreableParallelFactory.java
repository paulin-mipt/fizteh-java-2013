package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableProviderStoreableFactory;

import java.io.IOException;

public class TableProviderStoreableParallelFactory extends TableProviderStoreableFactory {
    public TableProvider create(String path) throws IOException {
        checkPath(path);
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setPath(false, path);
        TableStoreableParallelBuilder dataBaseBuilder = new TableStoreableParallelBuilder();
        storageBuilder.setDataBaseBuilder(dataBaseBuilder);
        return new TableProviderStoreableParallel<TableStoreableParallel>(storageBuilder.construct(), dataBaseBuilder);
    }
}
