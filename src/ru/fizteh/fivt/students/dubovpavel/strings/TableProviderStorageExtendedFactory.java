package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;

import java.io.File;

public class TableProviderStorageExtendedFactory implements TableProviderFactory {
    public static void check(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException();
        }
        File corresponding = new File(dir);
        if (corresponding.isFile()) {
            throw new IllegalArgumentException();
        }
        if (!corresponding.isDirectory()) {
            throw new RuntimeException();
        }
    }

    public TableProvider create(String dir) {
        check(dir);
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setPath(false, dir);
        storageBuilder.setDataBaseBuilder(new StringWrappedMindfulDataBaseMultiFileHashMapBuilder());
        Dispatcher dummy = new Dispatcher(false);
        storageBuilder.setDispatcher(dummy);
        return new StringTableProviderStorageExtended(storageBuilder.construct());
    }
}
