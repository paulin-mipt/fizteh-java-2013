package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend;


import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Table;

public interface ExtendTable extends Table, AutoCloseable {
    
    int getChangedValuesNumber();

    void loadAll() throws IOException;

    void close();

    boolean isClosed();
}
