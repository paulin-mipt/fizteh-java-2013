package ru.fizteh.fivt.students.belousova.multifilehashmap;

import java.io.File;

public class MultiFileTableProviderFactory implements ChangesCountingTableProviderFactory {
    @Override
    public MultiFileTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("null directory");
        }
        if (dir.trim().isEmpty()) {
            throw new IllegalArgumentException("empty directory");
        }

        File file = new File(dir);
        file.mkdir();

        return new MultiFileTableProvider(file);
    }
}
