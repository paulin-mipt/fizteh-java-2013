package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class MkdirCommand extends AbstractCommand<ShellState> {
    public MkdirCommand() {
        super("mkdir", 1);
    }

    public void execute(String argumentsLine, ShellState shellState) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        shellState.fileCommander.createNewDirectory(arguments[0]);
    }
}
