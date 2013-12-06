package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellMkdir extends ShellIsItCommand{
	
	private FileFunctions fileFunctions;
	
	public ShellMkdir(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
		setName("mkdir");
		setNumberOfArgs(2);
	}
	
	public void run() throws IOException{
		FileFunctions.createDir(arguments[1]);
	}

}
