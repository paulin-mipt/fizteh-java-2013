package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;

public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable {

    public static final String LEGAL_NAME = "[^*?\"<>|]+";
    private Set<ExtendProvider> providers = new HashSet<>();
    private volatile boolean isClosed = false;

    @Override
    public ExtendProvider create(String dataBaseDir) throws IOException {
        if (isClosed) {
            throw new IllegalStateException("access to closed object");
        }

        if (dataBaseDir == null || dataBaseDir.trim().isEmpty() || !dataBaseDir.matches(LEGAL_NAME)) {
           throw new IllegalArgumentException("dir not defined or has illegal name");
        }
        
        File directory = new File(dataBaseDir);
        
        if (!directory.exists()) {
            throw new IOException(dataBaseDir + " directory not exists");
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException(dataBaseDir + " not a directory");    
        }
        ExtendProvider provider = new MyTableProvider(directory);
        providers.add(provider);
        return provider;
    }

    @Override
    public void close() {
        if (!isClosed) {
            for (ExtendProvider provider: providers) {
                provider.close();
            }
            isClosed = true;
        }
    }
}
