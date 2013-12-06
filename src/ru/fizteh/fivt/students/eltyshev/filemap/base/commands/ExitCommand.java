package ru.fizteh.fivt.students.eltyshev.filemap.base.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

public class ExitCommand<State extends BaseFileMapShellState> extends AbstractCommand<State> {
    public ExitCommand() {
        super("exit", "exit");
    }

    public void executeCommand(String params, State state) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (state.getTable() != null) {
            state.rollback();
        }
        System.exit(0);
    }
}
