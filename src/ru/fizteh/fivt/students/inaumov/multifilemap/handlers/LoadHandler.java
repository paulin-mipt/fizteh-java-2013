package ru.fizteh.fivt.students.inaumov.multifilemap.handlers;

import ru.fizteh.fivt.students.inaumov.filemap.builders.TableBuilder;
import ru.fizteh.fivt.students.inaumov.filemap.handlers.ReadHandler;

import java.io.File;
import java.io.IOException;

public class LoadHandler {
    public static void loadTable(TableBuilder builder) throws IOException {
        File tableDir = builder.getTableDir();
        if (tableDir.listFiles() == null) {
            return;
        }

        for (final File bucket: tableDir.listFiles()) {
            if (bucket.isFile()) {
                continue;
            }

            if (bucket.listFiles().length == 0) {
                throw new IllegalArgumentException("empty bucket");
            }

            for (final File file: bucket.listFiles()) {
                builder.setCurrentFile(file);
                ReadHandler.loadFromFile(file.getAbsolutePath(), builder);
            }
        }
    }
}
