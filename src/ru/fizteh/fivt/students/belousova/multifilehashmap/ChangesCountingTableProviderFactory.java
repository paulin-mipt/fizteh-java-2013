package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public interface ChangesCountingTableProviderFactory extends TableProviderFactory {
    ChangesCountingTableProvider create(String dir);
}
