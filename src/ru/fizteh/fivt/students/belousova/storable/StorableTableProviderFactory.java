package ru.fizteh.fivt.students.belousova.storable;

import java.io.File;
import java.io.IOException;

public class StorableTableProviderFactory implements ChangesCountingTableProviderFactory {
    @Override
    public StorableTableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path to storage isn't set");
        }
        if (path.trim().isEmpty()) {
            throw new IllegalArgumentException("empty directory");
        }

        return new StorableTableProvider(new File(path));
    }
}
