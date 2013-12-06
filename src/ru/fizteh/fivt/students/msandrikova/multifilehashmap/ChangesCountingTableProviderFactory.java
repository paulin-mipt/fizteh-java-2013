package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public interface ChangesCountingTableProviderFactory extends TableProviderFactory {
    @Override
    ChangesCountingTableProvider create(String dir);
}
