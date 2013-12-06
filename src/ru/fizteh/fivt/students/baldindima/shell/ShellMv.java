package ru.fizteh.fivt.students.baldindima.shell;

import java.io.File;
import java.io.IOException;

public class ShellMv extends ShellIsItCommand {
	private FileFunctions fileFunctions;
	public ShellMv(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
		setName("mv");
		setNumberOfArgs(3);
	}
	
	public void run() throws IOException{
		FileFunctions.move(arguments);
		
	}


}
