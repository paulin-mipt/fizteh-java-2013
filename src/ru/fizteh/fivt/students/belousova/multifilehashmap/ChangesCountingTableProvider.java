package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;

public interface ChangesCountingTableProvider extends TableProvider {
    @Override
    ChangesCountingTable getTable(String name);
}
