package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.students.eltyshev.filemap.base.commands.BaseFileMapShellState;

import java.io.IOException;

public interface BaseDatabaseShellState<Table, Key, Value> extends BaseFileMapShellState<Table, Key, Value> {
    public Table useTable(String name);

    public void dropTable(String name) throws IOException;

    public Table createTable(String parameters);

    public String getActiveTableName();
}
