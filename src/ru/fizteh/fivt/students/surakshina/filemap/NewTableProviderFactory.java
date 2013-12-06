package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class NewTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private HashSet<AutoCloseable> providers = new HashSet<>();
    private CloseState state = new CloseState();

    public NewTableProviderFactory() {
    }

    @Override
    public TableProvider create(String path) throws IOException {
        state.checkClosed();
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("No directory");
        }
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Can't create a directory");
            }
        } else {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("Not a directory");
            }
        }
        NewTableProvider provider = new NewTableProvider(directory);
        providers.add(provider);
        return provider;
    }

    @Override
    public void close() throws Exception {
        for (AutoCloseable object : providers) {
            object.close();
        }
        state.setClose();
    }

}
