package ru.fizteh.fivt.students.msandrikova.storeable;

import ru.fizteh.fivt.storage.structured.Table;

public interface ChangesCountingTable extends Table {
    int unsavedChangesCount();

    void close();
}
