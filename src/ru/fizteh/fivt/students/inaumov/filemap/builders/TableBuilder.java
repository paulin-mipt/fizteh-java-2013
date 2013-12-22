package ru.fizteh.fivt.students.inaumov.filemap.builders;

import java.io.File;
import java.util.Set;

public interface TableBuilder {
    String get(String key);

    void put(String key, String value);

    Set<String> getKeys();

    File getTableDir();

    void setCurrentFile(File currentFile);
}
