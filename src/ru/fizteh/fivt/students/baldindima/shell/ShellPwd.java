package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellPwd extends ShellIsItCommand{
	private FileFunctions fileFunctions;
	public ShellPwd(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
		setName("pwd");
		setNumberOfArgs(1);
	}
	
	public void run() throws IOException{
		System.out.println(FileFunctions.getCurrentDir());
	}
	

}
