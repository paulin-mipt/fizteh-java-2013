package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellCp extends ShellIsItCommand {
	private FileFunctions fileFunctions;
	public ShellCp(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
		setName("cp");
		setNumberOfArgs(3);
	}
	
	public void run() throws IOException{
		FileFunctions.readyToCopy(arguments);
	}

}
