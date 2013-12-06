package ru.fizteh.fivt.students.asaitgalin.storable.extensions;

import ru.fizteh.fivt.storage.structured.Table;

public interface ExtendedTable extends Table {
    int getChangesCount();
}
