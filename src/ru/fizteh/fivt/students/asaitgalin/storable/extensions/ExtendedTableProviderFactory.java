package ru.fizteh.fivt.students.asaitgalin.storable.extensions;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public interface ExtendedTableProviderFactory extends TableProviderFactory {
    @Override
    ExtendedTableProvider create(String dir) throws IOException;
}
