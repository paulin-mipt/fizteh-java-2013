package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;

public class Cd implements BasicCommand {
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {	
		File goToDirectory = Utils.getFile(arguments[0], usedShell);

		if (goToDirectory.isDirectory()) {
			usedShell.curShell.changeCurrentDirectory(goToDirectory.getPath());
		} else {
			throw new IOException(arguments[0] + ": no such directory");	
		}
	}
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "cd";
	}
}