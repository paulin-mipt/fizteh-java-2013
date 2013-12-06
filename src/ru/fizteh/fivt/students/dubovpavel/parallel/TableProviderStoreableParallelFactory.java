package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.strings.TableProviderStorageExtendedFactory;

import java.io.IOException;

public class TableProviderStoreableParallelFactory implements TableProviderFactory {
    public TableProvider create(String path) throws IOException {
        try {
            TableProviderStorageExtendedFactory.check(path);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IOException(e.getMessage());
        }
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setPath(false, path);
        TableStoreableParallelBuilder dataBaseBuilder = new TableStoreableParallelBuilder();
        storageBuilder.setDataBaseBuilder(dataBaseBuilder);
        Dispatcher dummy = new Dispatcher(false);
        storageBuilder.setDispatcher(dummy);
        return new TableProviderStoreableParallel<TableStoreableParallel>(storageBuilder.construct(), dataBaseBuilder);
    }
}
