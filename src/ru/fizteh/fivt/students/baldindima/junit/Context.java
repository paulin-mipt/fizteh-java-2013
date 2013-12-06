package ru.fizteh.fivt.students.baldindima.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Context {
    public TableProvider provider;
    public Table table;

    public Context(TableProvider newProvider) {
        provider = newProvider;
        table = null;
    }

    public int getChanges() {
        if (table != null) {
            return ((DataBase) table).countCommits();
        }
        return 0;
    }
}
