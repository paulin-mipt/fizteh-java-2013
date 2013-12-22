package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

import java.io.IOException;

public class MvCommand extends AbstractCommand<ShellState> {
    public MvCommand() {
        super("mv", 2);
    }

    public void execute(String argumentsLine, ShellState shellState) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        try {
            shellState.fileCommander.moveFiles(arguments[0], arguments[1]);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
