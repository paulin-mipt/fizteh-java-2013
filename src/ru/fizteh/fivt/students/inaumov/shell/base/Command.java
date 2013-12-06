package ru.fizteh.fivt.students.inaumov.shell.base;

import ru.fizteh.fivt.students.inaumov.shell.exceptions.UserInterruptionException;

public interface Command<State> {
	public String getName();

    public int getArgumentsNumber();

	public void execute(String[] args, State state) throws UserInterruptionException;
}
