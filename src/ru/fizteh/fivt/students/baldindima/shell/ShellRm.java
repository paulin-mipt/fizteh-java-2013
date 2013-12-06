package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellRm extends ShellIsItCommand {
	private FileFunctions fileFunctions;
	
	public ShellRm(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
		setName("rm");
		setNumberOfArgs(2);
	}
	
	public void run() throws IOException {
		FileFunctions.delete(arguments[1]);
	}


}
