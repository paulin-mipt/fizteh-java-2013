package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;

import java.io.IOException;

public interface MultiFileMapShellState<Table, Key, Value> extends FileMapShellState<Table, Key, Value> {
    Table createTable(String tableName);

    void dropTable(String tableName) throws IOException;

    Table useTable(String tableName);

    String getCurrentTableName();

    String[] parseCreateCommand(String argumentsLine);
}
