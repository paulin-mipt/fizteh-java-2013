package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class StoreableTableProviderFactory implements TableProviderFactory {
    @Override
    public StoreableTableProvider create(String dirName) throws IOException {
        if (dirName != null) {
            dirName = dirName.trim();
        }

        if (dirName == null || dirName.isEmpty()) {
            throw new IllegalArgumentException("CREATE ERROR: incorrect directory name");
        }
        if (!dirName.matches("[^><\"?|*.]*")) {
            throw new RuntimeException("CREATE ERROR: illegal symbol in table name");
        }
        if (!new File(dirName).exists()) {
            throw new IOException("CREATE ERROR: not existing directory");
        }
        if (!new File(dirName).isDirectory()) {
            throw new IllegalArgumentException("CREATE ERROR: table is not directory");
        }

        return new StoreableTableProvider();
    }
}
