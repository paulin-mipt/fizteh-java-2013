package ru.fizteh.fivt.students.eltyshev.storable.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.filemap.base.ContainerState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private ContainerState state = ContainerState.WORKING;
    private List<DatabaseTableProvider> providers = new ArrayList<DatabaseTableProvider>();

    @Override
    public synchronized TableProvider create(String directory) throws IOException {
        state.checkOperationsAllowed();
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }

        if (directory.trim().isEmpty()) {
            throw new IllegalArgumentException("directory's name cannot be empty");
        }

        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("cannot create database in file. Provide a directory, please");
        }

        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdirs()) {
                throw new IOException("provider is unavailable");
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
        state = ContainerState.CLOSED;
    }
}
