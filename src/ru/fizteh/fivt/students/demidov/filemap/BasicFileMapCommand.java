package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;
import ru.fizteh.fivt.students.demidov.basicclasses.BasicState;
import ru.fizteh.fivt.students.demidov.shell.Shell;
import ru.fizteh.fivt.students.demidov.shell.ShellInterruptionException;

public abstract class BasicFileMapCommand implements BasicCommand {
	protected BasicState currentState;
	private String commandName;
	private int argumentsNumber;
	
	public BasicFileMapCommand(BasicState currentState, String commandName, int argumentsNumber) {
		this.currentState = currentState;
		this.commandName = commandName;
		this.argumentsNumber = argumentsNumber;
	}
	
	abstract public void executeCommand(String[] arguments, Shell usedShell) throws IOException, ShellInterruptionException;
	
	public int getNumberOfArguments() {
		return argumentsNumber;
	}
	
	public String getCommandName() {
		return commandName;
	}
}
