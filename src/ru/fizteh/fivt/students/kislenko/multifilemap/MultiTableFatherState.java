package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.filemap.FatherState;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MultiTableFatherState extends FatherState {
    public abstract boolean alrightCreate(String tableName, AtomicReference<Exception> checkingException,
                                          AtomicReference<String> message);

    public abstract void createTable(String[] tableParameters) throws Exception;

    public abstract void deleteTable(String tableName) throws Exception;

    public abstract boolean needToChangeTable(String newTableName);

    public abstract boolean isTransactional();

    public abstract void dumpOldTable() throws IOException;

    public abstract void changeTable(String tableName, AtomicReference<String> message) throws Exception;

    public abstract int getTableChangeCount();

    public abstract boolean autoCommit();

    public abstract int commitCurrentTable() throws IOException;
}
