package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

import java.util.ArrayList;

public class UseCommand<Table, Key, Value, State extends BaseDatabaseShellState<Table, Key, Value>> extends AbstractCommand<State> {
    public UseCommand() {
        super("use", "use <table name>");
    }

    public void executeCommand(String params, State shellState) {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments!");
        }
        if (parameters.size() < 1) {
            throw new IllegalArgumentException("argument missing");
        }
        Table newTable = null;
        try {
            newTable = shellState.useTable(parameters.get(0));
        } catch (IllegalArgumentException e) {
            System.err.println(String.format("wrong type (%s)", e.getMessage()));
            return;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(String.format("%s not exists", parameters.get(0)));
            return;
        }

        System.out.println(String.format("using %s", shellState.getActiveTableName()));
    }
}
