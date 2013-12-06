package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.strings.TableProviderStorageExtendedFactory;

import java.io.IOException;

public class TableProviderStoreableFactory implements TableProviderFactory {
    public TableProvider create(String dir) throws IOException {
        try {
            TableProviderStorageExtendedFactory.check(dir);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IOException(e.getMessage());
        }
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setPath(false, dir);
        TableStoreableBuilder dataBaseBuilder = new TableStoreableBuilder();
        storageBuilder.setDataBaseBuilder(dataBaseBuilder);
        Dispatcher dummy = new Dispatcher(false);
        storageBuilder.setDispatcher(dummy);
        return new TableProviderStoreable<TableStoreable>(storageBuilder.construct(), dataBaseBuilder);
    }
}
