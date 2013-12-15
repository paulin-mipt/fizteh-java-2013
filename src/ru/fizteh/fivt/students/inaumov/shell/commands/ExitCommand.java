package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;
import ru.fizteh.fivt.students.inaumov.shell.exceptions.UserInterruptionException;

public class ExitCommand<State> extends AbstractCommand<State> {
    public ExitCommand() {
        super("exit", 0);
    }

    public void execute(String argumentsLine, State shellState) throws UserInterruptionException {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        throw new UserInterruptionException();
    }
}
