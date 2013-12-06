package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions;

import ru.fizteh.fivt.storage.strings.Table;

public interface ChangesCountingTable extends Table {
    int getChangesCount();
}
