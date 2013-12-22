package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public MyTableProvider create(String dir) {
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException("Incorrect database name.");
        }
        File file = new File(dir);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Database must be a directory.");
        }
        return new MyTableProvider();
    }
}
