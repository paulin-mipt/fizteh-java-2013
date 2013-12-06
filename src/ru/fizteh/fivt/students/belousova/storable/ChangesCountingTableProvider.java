package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.TableProvider;

public interface ChangesCountingTableProvider extends TableProvider {
    @Override
    ChangesCountingTable getTable(String name);
}
