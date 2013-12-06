package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

public interface ChangesCountingTable extends Table {
    int getChangesCount();
}
