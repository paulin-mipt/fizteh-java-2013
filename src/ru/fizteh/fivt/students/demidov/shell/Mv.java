package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicCommand;

public class Mv implements BasicCommand {
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {	
		File source = Utils.getFile(arguments[0], usedShell);
		File destination = Utils.getFile(arguments[1], usedShell);
		
		if ((source.exists()) && (source.getPath().equals(destination.getPath()))) {
			return;
		}
		
		if ((source.exists()) && (source.isDirectory()) && (!destination.exists()) && (source.getParent().equals(destination.getParent()))) {
			if (!source.renameTo(destination)) {
				throw new IOException("unable to move " + source.getPath() + " to " + destination.getPath());
			}
			return;
		}
		
		(new Cp()).executeCommand(arguments, usedShell);
		(new Rm()).executeCommand(Arrays.copyOfRange(arguments, 0, 1), usedShell);
	}
	public int getNumberOfArguments() {
		return 2;
	}	
	public String getCommandName() {
		return "mv";
	}
}
