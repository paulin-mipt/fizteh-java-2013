package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class PwdCommand extends AbstractCommand<ShellState> {
    public PwdCommand() {
        super("pwd", 0);
    }

    public void execute(String argumentsLine, ShellState shellState) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        String workingDir = shellState.fileCommander.getCurrentDirectory();
        System.out.println(workingDir);
    }
}
