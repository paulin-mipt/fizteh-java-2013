package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

import java.io.IOException;

public class StringTableProviderStorageExtended
        extends TableProviderStorageExtended<StringWrappedMindfulDataBaseMultiFileHashMap> implements TableProvider {
    public StringTableProviderStorageExtended(Storage storage) {
        super(storage);
    }

    public void removeTable(String name) {
        try {
            super.removeTableExplosive(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Table createTable(String name) {
        try {
            return super.createTableExplosive(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
