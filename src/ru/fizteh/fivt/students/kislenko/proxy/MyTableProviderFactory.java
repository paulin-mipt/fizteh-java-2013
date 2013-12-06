package ru.fizteh.fivt.students.kislenko.proxy;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable {
    Set<MyTableProvider> providers = new HashSet<MyTableProvider>();
    boolean closed = false;

    @Override
    public MyTableProvider create(String path) throws IOException {
        if (closed) {
            throw new IllegalStateException("Factory was closed.");
        }
        if (path == null || path.trim().equals("")) {
            throw new IllegalArgumentException("Incorrect database name.");
        }
        if (path.contains(".")) {
            throw new RuntimeException("Incorrect database name.");
        }
        File file = new File(path.trim());
        if (file.isFile()) {
            throw new IllegalArgumentException("Database must be a directory.");
        }
        if (!file.exists()) {
            throw new IOException("Database directory doesn't exist.");
        }
        MyTableProvider provider = new MyTableProvider(path);
        providers.add(provider);
        return provider;
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            return;
        }
        for (MyTableProvider provider : providers) {
            if (!provider.isClosed()) {
                provider.close();
            }
        }
        closed = true;
    }
}
