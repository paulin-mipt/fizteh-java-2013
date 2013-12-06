package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StoreableTableProviderFactory implements TableProviderFactory, AutoCloseable {

    protected boolean autoCommit;
    private boolean isClosed;
    private final Set<StoreableTableProvider> providerSet;

    //autoCommit disabled by default
    public StoreableTableProviderFactory() {
        autoCommit = false;
        providerSet = new HashSet<StoreableTableProvider>();
        isClosed = false;
    }

    public StoreableTableProviderFactory(boolean autoCommit) {
        this.autoCommit = autoCommit;
        providerSet = new HashSet<StoreableTableProvider>();
        isClosed = false;
    }

    public StoreableTableProvider create(String dir) throws IOException {
        checkClosed();
        StoreableTableProvider provider = null;

        if ((dir == null) || (dir.trim().isEmpty())) {
            throw new IllegalArgumentException("Directory not specified");
        }

        if ((!(new File(dir)).exists()) && (!(new File(dir)).mkdirs())) {
            throw new IOException("Unable to create " + dir);
        }

        try {
            provider = new StoreableTableProvider(dir, autoCommit);
            providerSet.add(provider);

            return provider;
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public void close() {
        if (isClosed) {
            return;
        }

        for (StoreableTableProvider provider : providerSet) {
            provider.close();
        }

        isClosed = true;
    }

    public void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("trying to operate on a closed table provider");
        }
    }
}

