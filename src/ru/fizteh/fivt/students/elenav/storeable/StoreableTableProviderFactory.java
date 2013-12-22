package ru.fizteh.fivt.students.elenav.storeable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class StoreableTableProviderFactory implements TableProviderFactory, AutoCloseable {
    
    private volatile boolean isClosed = false;
    private volatile HashSet<StoreableTableProvider> providers = new HashSet<>();
    
    @Override
    public synchronized TableProvider create(String path) throws IOException {
        checkIsNotClosed();
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("can't create TableProvider: not existing directory");
        }
        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new IOException("can't create TableProvider: unknown error");
            } 
        } else {
            if (!f.isDirectory()) {
                throw new IllegalArgumentException("can't create TableProvider: it is not directory");
            }
        }
        StoreableTableProvider p = new StoreableTableProvider(f, System.out);
        providers.add(p);
        return p;
    }

    @Override
    public void close() throws Exception {
        isClosed = true;
        for (StoreableTableProvider s : providers) {
            s.close();
        }
    }
    
    private void checkIsNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("table provider factory is closed");
        }
    }

}
