package ru.fizteh.fivt.students.kislenko.junit;

import ru.fizteh.fivt.students.kislenko.multifilemap.MultiTableFatherState;

import java.io.IOException;

public abstract class TransactionalFatherState extends MultiTableFatherState {
    public abstract boolean hasCurrentTable();

    public abstract void dumpCurrentTable() throws IOException;

    public abstract int rollbackCurrentTable();

    public abstract int getCurrentTableSize();
}
