package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class StoreableTableProviderFactory implements TableProviderFactory {
    @Override
    public StoreableTableProvider create(String dir) throws IOException {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("dir is null");
        }

        File dbDirectory = new File(dir);
        if (dbDirectory.exists() && !dbDirectory.isDirectory()) {
            throw new IllegalArgumentException("not a dir");
        }
        if (!dbDirectory.exists()) {
            throw new IOException("no such directory");
        }

        return new StoreableTableProvider(dbDirectory, false);
    }
}