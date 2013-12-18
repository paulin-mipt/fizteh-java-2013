package ru.fizteh.fivt.students.eltyshev.servlet.shell;

import ru.fizteh.fivt.students.eltyshev.servlet.database.TransactionManager;
import ru.fizteh.fivt.students.eltyshev.servlet.server.DatabaseServer;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProvider;

public class ServletShellState extends StoreableShellState {
    public DatabaseServer server;

    public ServletShellState(DatabaseTableProvider provider) {
        super(provider);
        server = new DatabaseServer(new TransactionManager(provider));
    }
}
