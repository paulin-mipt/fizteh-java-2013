package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DBTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private volatile boolean isClosed = false;
    private Set<TableProvider> allProviders = new HashSet<>();

    @Override
    public TableProvider create(String rootDir) throws IOException {
        if (isClosed) {
            throw new IllegalStateException("TableProviderFactory was closed");
        }
        if (rootDir == null || rootDir.trim().isEmpty() || rootDir.contains("\0")) {
            throw new IllegalArgumentException("wrong directory name");
        }
        TableProvider newTableProvider = null;
        File file = new File(rootDir);
        newTableProvider = new DBTableProvider(file);
        allProviders.add(newTableProvider);
        return newTableProvider;
    }

    @Override
    public void close() {
        if (!isClosed) {
            for (TableProvider provider : allProviders) {
                ((DBTableProvider) provider).close();
            }
            isClosed = true;
        }
    }
}
