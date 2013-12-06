package ru.fizteh.fivt.students.demidov.shell;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;

public class Mkdir implements BasicCommand {
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {		
		if (!Utils.getFile(arguments[0], usedShell).mkdir()) {
			throw new IOException("unable to make directory " + arguments[0]);
		}
	}
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "mkdir";
	}
}