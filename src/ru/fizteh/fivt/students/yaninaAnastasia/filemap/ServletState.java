package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

public class ServletState extends MultiDBState {
    public DatabaseServer server;

    public ServletState(DatabaseTableProvider provider) {
        server = new DatabaseServer(new TransactionWorker(provider));
    }
}
