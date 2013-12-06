package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {

    @Override
    public MultiFileHashMapTableProvider create(String dir) {

        if (dir == null) {
            throw new IllegalArgumentException("null directory");
        }
        if (dir.trim().isEmpty()) {
            throw new IllegalArgumentException("empty directory");
        }

        File file = new File(dir);

        return new MultiFileHashMapTableProvider(file);
    }
}
