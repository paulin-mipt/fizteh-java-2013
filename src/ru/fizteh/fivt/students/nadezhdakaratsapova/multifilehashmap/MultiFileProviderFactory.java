package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;


public class MultiFileProviderFactory implements TableProviderFactory {

    public MultiFileHashMapProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("Not allowed name of DataBaseStorage");
        } else {
            File dataDirectory = new File(dir);
            if (!dataDirectory.exists()) {
                throw new IllegalArgumentException("The working directory doesn't exist");
            }
            if (!dataDirectory.isDirectory()) {
                throw new IllegalArgumentException("The root directory should be a directory");
            }
            MultiFileHashMapProvider newStorage;
            try {
                newStorage = new MultiFileHashMapProvider(dataDirectory.getCanonicalFile());
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            return newStorage;
        }
    }
}
