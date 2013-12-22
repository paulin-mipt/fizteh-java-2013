package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.strings.TableProviderStorageExtendedFactory;

import java.io.IOException;

public class TableProviderStoreableFactory implements TableProviderFactory {
    protected void checkPath(String path) throws IOException {
        try {
            TableProviderStorageExtendedFactory.check(path);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IOException(e.getMessage());
        }
    }

    public TableProvider create(String dir) throws IOException {
        checkPath(dir);
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setPath(false, dir);
        TableStoreableBuilder dataBaseBuilder = new TableStoreableBuilder();
        storageBuilder.setDataBaseBuilder(dataBaseBuilder);
        return new TableProviderStoreable<TableStoreable>(storageBuilder.construct(), dataBaseBuilder);
    }
}
