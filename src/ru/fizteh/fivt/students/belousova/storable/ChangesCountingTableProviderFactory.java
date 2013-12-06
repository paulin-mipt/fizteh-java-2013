package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public interface ChangesCountingTableProviderFactory extends TableProviderFactory {
    ChangesCountingTableProvider create(String dir) throws IOException;
}
