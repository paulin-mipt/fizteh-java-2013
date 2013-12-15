package ru.fizteh.fivt.students.inaumov.shell.base;

import ru.fizteh.fivt.students.inaumov.shell.exceptions.UserInterruptionException;

public interface Command<State> {
    String getName();

    int getArgumentsNumber();

    void execute(String argumentsLine, State state) throws UserInterruptionException;
}
