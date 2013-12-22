package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.students.eltyshev.multifilemap.MultifileMapUtils;
import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;

public class CreateCommand<Table, Key, Value, State extends BaseDatabaseShellState<Table, Key, Value>> extends AbstractCommand<State> {
    public CreateCommand() {
        super("create", "create <table name>");
    }

    public void executeCommand(String params, State shellState) {

        String tableName = MultifileMapUtils.parseTableName(params);

        Table newTable = null;
        try {
            newTable = shellState.createTable(params);
        } catch (IllegalArgumentException e) {
            System.err.println(String.format("wrong type (%s)", e.getMessage()));
            return;
        }
        if (newTable == null) {
            System.out.println(String.format("%s exists", tableName));
        } else {
            System.out.println("created");
        }
    }
}
