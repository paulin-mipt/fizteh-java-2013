package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

import java.io.IOException;

public class CpCommand extends AbstractCommand<ShellState> {
    public CpCommand() {
        super("cp", 2);
    }

    public void execute(String argumentsLine, ShellState shellState) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        try {
            shellState.fileCommander.copyFiles(arguments[0], arguments[1]);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
