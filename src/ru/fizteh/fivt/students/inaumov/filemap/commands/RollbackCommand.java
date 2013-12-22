package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class RollbackCommand<State extends FileMapShellState> extends AbstractCommand<State> {
    public RollbackCommand() {
        super("rollback", 0);
    }

    public void execute(String argumentsLine, State state) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        System.out.println(state.rollback());
    }
}
