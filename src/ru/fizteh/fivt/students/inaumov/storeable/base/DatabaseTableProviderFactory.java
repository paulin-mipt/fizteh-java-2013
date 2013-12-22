package ru.fizteh.fivt.students.inaumov.storeable.base;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.filemap.base.TableState;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private TableState state = TableState.WORKING;
    private List<DatabaseTableProvider> providers = new ArrayList<DatabaseTableProvider>();

    @Override
    public synchronized TableProvider create(String directory) throws IOException {
        state.checkAvailable();

        if (directory == null || directory.trim().isEmpty()) {
            throw new IllegalArgumentException("error: directory is null (or empty)");
        }

        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("error: database is placed in a file");
        }

        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdirs()) {
                throw new IOException("error: provider is unavailable");
            }
        }

        DatabaseTableProvider newProvider = new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
        providers.add(newProvider);

        return newProvider;
    }

    @Override
    public void close() throws Exception {
        for (DatabaseTableProvider provider : providers) {
            provider.close();
        }

        state = TableState.CLOSED;
    }
}
