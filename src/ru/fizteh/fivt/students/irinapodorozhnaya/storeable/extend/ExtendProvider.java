package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend;

import java.io.IOException;
import java.util.List;
import ru.fizteh.fivt.storage.structured.TableProvider;

public interface ExtendProvider extends  TableProvider, AutoCloseable {
    
    @Override
    ExtendTable getTable(String name);
    
    @Override
    ExtendTable createTable(String name, List<Class<?>> columnType)
        throws IOException;

    void close();
}
