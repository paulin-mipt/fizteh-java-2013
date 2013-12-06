package ru.fizteh.fivt.students.kislenko.parallels;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public MyTableProvider create(String path) throws IOException {
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
        return new MyTableProvider();
    }
}
