package ru.fizteh.fivt.students.inaumov.shell;

import ru.fizteh.fivt.students.inaumov.shell.base.Command;

public class ShellUtils {
    public static void checkArgumentsNumber(Command command, int argumentsNumber) {
        if (command.getArgumentsNumber() == -1) {
            return;
        }

        if (command.getArgumentsNumber() != argumentsNumber) {
            throw new IllegalArgumentException("error: command " + command.getName()
                    + ": expected " + command.getArgumentsNumber() + " arguments");
        }

        return;
    }
}
