package ru.fizteh.fivt.students.eltyshev.filemap.base.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;


public class RollbackCommand<State extends BaseFileMapShellState> extends AbstractCommand<State> {
    public RollbackCommand() {
        super("rollback", "rollback");
    }

    public void executeCommand(String params, State state) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }
        System.out.println(state.rollback());
    }
}
