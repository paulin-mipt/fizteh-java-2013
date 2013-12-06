package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {
    @Override
    public MultiFileHashMapTableProvider create(String dirName) {
        if (dirName == null || dirName.equals("")) {
            throw new IllegalArgumentException("CREATE ERROR: incorrect directory name");
        }
        if (!new File(dirName).isDirectory()) {
            throw new IllegalArgumentException("CREATE ERROR: table is not directory");
        }

        return new MultiFileHashMapTableProvider();
    }
}
