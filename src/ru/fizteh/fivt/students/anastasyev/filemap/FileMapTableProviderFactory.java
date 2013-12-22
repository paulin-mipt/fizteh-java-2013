package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileMapTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private ArrayList<FileMapTableProvider> fileMapTableProviders = new ArrayList<FileMapTableProvider>();
    private boolean isOpen = true;

    @Override
    public FileMapTableProvider create(String dir) throws IOException {
        if (!isOpen) {
            throw new IllegalStateException("TableProviderFactory is already closed");
        }
        if (dir == null || dir.trim().trim().isEmpty()) {
            throw new IllegalArgumentException("Directory path can't be null");
        }
        File fileDir = new File(dir.trim());
        if (fileDir.isFile()) {
            throw new IllegalArgumentException("Wrong directory path");
        }
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                throw new IOException("Can't create directory");
            }
        }
        FileMapTableProvider fileMapTableProvider = null;
        try {
            fileMapTableProvider = new FileMapTableProvider(fileDir.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        fileMapTableProviders.add(fileMapTableProvider);
        return fileMapTableProvider;
    }

    @Override
    public void close() {
        if (!isOpen) {
            throw new IllegalStateException("TableProviderFactory is already closed");
        }
        for (FileMapTableProvider provider : fileMapTableProviders) {
            if (provider.isOpen()) {
                provider.close();
            }
        }
    }
}
