package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellDir extends ShellIsItCommand {
	private FileFunctions fileFunctions;
	public ShellDir(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
		setName("dir");
		setNumberOfArgs(1);
	}
	
	public void run() throws IOException{
		FileFunctions.printDir();
	}

}
