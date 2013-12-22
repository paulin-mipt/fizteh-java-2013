package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

import java.io.IOException;
import java.util.ArrayList;

public class DropCommand<State extends BaseDatabaseShellState> extends AbstractCommand<State> {
    public DropCommand() {
        super("drop", "drop <table name");
    }

    public void executeCommand(String params, State shellState) {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("wrong type (too many arguments)");
        }
        if (parameters.size() < 1) {
            throw new IllegalArgumentException("wrong type (argument missing)");
        }

        try {
            shellState.dropTable(parameters.get(0));
            System.out.println("dropped");
        } catch (IOException | IllegalStateException e) {
            System.err.println(String.format("%s", e.getMessage()));
        }
    }
}
