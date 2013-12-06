package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;

public class Rm implements BasicCommand {
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {		
		File source = Utils.getFile(arguments[0], usedShell);	

		if (source.exists()) {
			Utils.deleteFileOrDirectory(source);
		} else {
			throw new IOException(arguments[0] + " doesn't exist");
		}
	}
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "rm";
	}
}
