package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class CdCommand extends AbstractCommand<ShellState> {
    public CdCommand() {
        super("cd", 1);
    }

    public void execute(String argumentsLine, ShellState shellState) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        String newPath = arguments[0];
        shellState.fileCommander.setCurrentDirectory(newPath);
    }
}
