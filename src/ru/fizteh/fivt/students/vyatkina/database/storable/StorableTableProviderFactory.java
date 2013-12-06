package ru.fizteh.fivt.students.vyatkina.database.storable;


import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.database.logging.CloseState;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;

import static ru.fizteh.fivt.students.vyatkina.database.superior.SuperTableProviderFactory.directoryCheck;

public class StorableTableProviderFactory implements TableProviderFactory, Closeable {

    private final CloseState closeState;
    private final HashSet<Closeable> derivatives = new HashSet();

    public StorableTableProviderFactory() {
        closeState = new CloseState(this + " is closed");
    }

    @Override
    public TableProvider create(String path) throws IOException {
        closeState.isClosedCheck();
        StorableTableProviderImp tableProvider = new StorableTableProviderImp(directoryCheck(path));
        derivatives.add(tableProvider);
        return tableProvider;
    }

    @Override
    public void close() throws IOException {
        if (closeState.isAlreadyClosed()) {
            return;
        }
        for (Closeable o : derivatives) {
            o.close();
        }
        closeState.close();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
