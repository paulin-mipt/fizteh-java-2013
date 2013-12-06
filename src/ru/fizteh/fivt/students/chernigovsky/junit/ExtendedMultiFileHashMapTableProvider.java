package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;

public interface ExtendedMultiFileHashMapTableProvider extends TableProvider {
    File getDbDirectory();
    ExtendedMultiFileHashMapTable getTable(String name);
    ExtendedMultiFileHashMapTable createTable(String name);
}
