package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Table;

public interface ChangesCountingTable extends Table {
    int getChangesCount();
}
