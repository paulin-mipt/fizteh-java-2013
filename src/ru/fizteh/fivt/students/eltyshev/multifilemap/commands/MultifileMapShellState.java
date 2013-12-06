package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.eltyshev.filemap.base.commands.FileMapShellState;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

import java.util.List;

public class MultifileMapShellState extends FileMapShellState implements BaseDatabaseShellState<Table, String, String> {
    public TableProvider tableProvider;

    @Override
    public Table useTable(String name) {
        table = tableProvider.getTable(name);
        return table;
    }

    @Override
    public void dropTable(String name) {
        tableProvider.removeTable(name);
        table = null;
    }

    @Override
    public Table createTable(String parameters) {
        List<String> params = CommandParser.parseParams(parameters);
        return tableProvider.createTable(params.get(0));
    }

    @Override
    public String getActiveTableName() {
        return table.getName();
    }
}
