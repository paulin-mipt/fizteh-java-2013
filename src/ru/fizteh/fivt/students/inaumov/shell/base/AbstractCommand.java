package ru.fizteh.fivt.students.inaumov.shell.base;

import ru.fizteh.fivt.students.inaumov.shell.exceptions.UserInterruptionException;

public abstract class AbstractCommand<State> implements Command<State> {
	private final String name;
	private final int argsNumber;
	
	public AbstractCommand(String name, int argsNumber) {
		this.name = name;
		this.argsNumber = argsNumber;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArgumentsNumber() {
		return argsNumber;
	}

	public abstract void execute(String[] args, State state) throws UserInterruptionException;
}
