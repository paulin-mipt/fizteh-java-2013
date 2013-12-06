package ru.fizteh.fivt.students.elenav.states;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.students.elenav.storeable.StoreableTableState;

public interface Provider {

    File getWorkingDirectory();

    void removeTable(String name) throws IOException;

    Object getTable(String name);

    FilesystemState createTable(String string);

    StoreableTableState createTable(String string, List<Class<?>> identifyTypes)  throws IOException;

    void use(FilesystemState table) throws IOException;

    
}
