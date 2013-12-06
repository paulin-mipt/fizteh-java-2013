package ru.fizteh.fivt.students.demidov.shell;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Exit implements BasicCommand {
	public void executeCommand(String[] arguments, Shell usedShell) throws ShellInterruptionException {    
		throw new ShellInterruptionException();
	}	
	public int getNumberOfArguments() {
		return 0;
	}	
	public String getCommandName() {
		return "exit";
	}
}
