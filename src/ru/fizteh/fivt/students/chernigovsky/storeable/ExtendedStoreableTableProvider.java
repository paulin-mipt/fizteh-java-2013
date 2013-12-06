package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExtendedStoreableTableProvider extends TableProvider {
    File getDbDirectory();
    ExtendedStoreableTable getTable(String name);
    ExtendedStoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException;
}