package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class Context {
    public TableProvider provider;
    public Table table;

    public Context(TableProvider newProvider) {
        provider = newProvider;
        table = null;
    }

    public int getChanges() {
        if (table != null) {
            return ((DataBase) table).getNewKeys();
        }
        return 0;
    }
}
