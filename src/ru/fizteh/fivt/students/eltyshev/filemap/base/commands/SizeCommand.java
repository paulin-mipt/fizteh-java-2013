package ru.fizteh.fivt.students.eltyshev.filemap.base.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

public class SizeCommand<State extends BaseFileMapShellState> extends AbstractCommand<State> {
    public SizeCommand() {
        super("size", "size");
    }

    public void executeCommand(String params, State shellState) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (shellState.getTable() == null) {
            System.err.println("no table");
            return;
        }
        int size = shellState.size();
        System.out.println(size);
    }
}
