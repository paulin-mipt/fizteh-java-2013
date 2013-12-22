package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class FileMapProviderFactory implements TableProviderFactory, AutoCloseable {

    HashSet<FileMapProvider> setFileMapProvider = new HashSet<FileMapProvider>();
    volatile boolean isFactoryClose = false;

    public TableProvider create(String dir) throws IOException {
        if (isFactoryClose) {
            throw new IllegalStateException("factory is closed");
        }
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException();
        }
        File file = null;
        try {
            file = new File(dir);
        } catch (Exception e) {
            throw new IOException(dir + " directory doesn't open");
        }
        try {
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    throw new IOException(dir + " not create");
                }
            }
        } catch (Exception e) {
            throw new IOException(dir + " not exists (exception) " + e.getMessage());
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(dir + " isn't directory");
        }
        Path pathTables = Paths.get(".").resolve(dir);
        try {
            FileMapProvider provider = new FileMapProvider(pathTables.toFile().getCanonicalPath());
            setFileMapProvider.add(provider);
            return provider;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void close() {
        if (!isFactoryClose) {
            for (FileMapProvider provider: setFileMapProvider) {
                try {
                    provider.close();
                } catch (IllegalStateException e) {
                    //pass
                }
            }
            isFactoryClose = true;
        }
    }

}
