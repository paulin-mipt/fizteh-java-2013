package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

public interface ChangesCountingTable extends Table {
    int unsavedChangesCount();
}
