package ru.fizteh.fivt.students.vyatkina.database.multitable;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.superior.SuperTableProviderFactory;

import java.io.IOException;
import java.nio.file.Path;

public class MultiTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        try {
            Path directory = SuperTableProviderFactory.directoryCheck(dir);
            return new MultiTableProvider(directory);
        }
        catch (IOException e) {
            throw new WrappedIOException(e.getMessage());
        }
    }
}
