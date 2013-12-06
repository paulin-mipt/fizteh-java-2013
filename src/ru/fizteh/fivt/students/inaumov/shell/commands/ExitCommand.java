package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import ru.fizteh.fivt.students.inaumov.shell.exceptions.UserInterruptionException;

public class ExitCommand<State> extends AbstractCommand<State> {
	public ExitCommand() {
		super("exit", 0);
	}
	
	public void execute(String[] args, State shellState) throws UserInterruptionException {
		throw new UserInterruptionException();
	}
}
