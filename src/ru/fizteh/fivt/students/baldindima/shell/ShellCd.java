package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellCd extends ShellIsItCommand {
	private FileFunctions fileFunctions;
	public ShellCd(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
		setName("cd");
		setNumberOfArgs(2);
	}
	
	public void run() throws IOException{
		FileFunctions.changeDir(arguments[1]);
	}

}
