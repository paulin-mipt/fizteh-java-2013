package ru.fizteh.fivt.students.inaumov.multifilemap.base;

import ru.fizteh.fivt.storage.strings.*;

import java.io.File;

public class DatabaseFactory implements TableProviderFactory {
    public TableProvider create(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("directory name can't be null or empty");
        }

        File databaseDir = new File(dir);

        if (databaseDir.isFile()) {
            throw new IllegalArgumentException("directory can't be a file");
        }

        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }

        return new DatabaseTableProvider(databaseDir.getAbsolutePath());
    }
}
