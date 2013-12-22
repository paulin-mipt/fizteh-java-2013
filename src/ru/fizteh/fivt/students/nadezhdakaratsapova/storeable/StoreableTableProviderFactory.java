package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StoreableTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private Set<StoreableTableProvider> tableProviders = new HashSet<StoreableTableProvider>();
    private boolean closed = false;

    public StoreableTableProvider create(String dir) throws IOException {
        checkNotClosed();
        if ((dir == null) || (dir.trim().isEmpty())) {
            throw new IllegalArgumentException("Not allowed name of DataBaseStorage");
        } else {
            checkNotClosed();
            File dataDirectory = new File(dir);
            if (!dataDirectory.exists()) {
                if (!dataDirectory.mkdir()) {
                    throw new IOException("The working directory is not exist");
                }
            }
            if (!dataDirectory.isDirectory()) {
                checkNotClosed();
                throw new IllegalArgumentException("The root directory should be a directory");
            }
            StoreableTableProvider newStorage = new StoreableTableProvider(dataDirectory);
            tableProviders.add(newStorage);
            return newStorage;
        }
    }

    public void close() throws IOException {
        if (!closed) {
            for (StoreableTableProvider tableProvider : tableProviders) {
                if (!tableProvider.isTableProviderClosed()) {
                    tableProvider.close();
                }
            }
            closed = true;
        }
    }

    public void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("the TableProvider is closed");
        }
    }
}
