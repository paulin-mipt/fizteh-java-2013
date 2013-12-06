package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTableProvider;
import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileTableProviderFactory implements ExtendedTableProviderFactory {
    @Override
    public ExtendedTableProvider create(String dir) throws IOException {
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("factory, create: directory name is invalid");
        }
        File dbDir = new File(dir);
        if (!dbDir.exists()) {
            if (!dbDir.mkdir()) {
                throw new IOException("factory, create: table provider unavailable");
            }
        } else {
            if (!dbDir.isDirectory()) {
                throw new IllegalArgumentException("factory, create: provided name is not directory");
            }
        }
        return new MultiFileTableProvider(new File(dir));
    }
}
