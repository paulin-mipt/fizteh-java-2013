package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;

public class Cp implements BasicCommand {
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {	
		File source = Utils.getFile(arguments[0], usedShell);
		File destination = Utils.getFile(arguments[1], usedShell);
		
		if (source.exists()) {
			if ((destination.exists()) || ((source.isFile()) && ((new File(destination.getParent())).exists()))) {
				Utils.copyFileOrDirectory(source, destination);
			} else {
				throw new IOException(destination.getPath() + " doesn't exist");
			}
		} else {
			throw new IOException(source.getPath() + " doesn't exist");
		}
	}
	public int getNumberOfArguments() {
		return 2;
	}	
	public String getCommandName() {
		return "cp";
	}
}
