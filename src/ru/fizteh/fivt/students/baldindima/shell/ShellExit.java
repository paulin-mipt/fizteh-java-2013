package ru.fizteh.fivt.students.baldindima.shell;


public class ShellExit extends ShellIsItCommand {
	public ShellExit(){
		setName("exit");
		setNumberOfArgs(1);
		
	}
	
	public void run() throws  ExitException{
		throw new ExitException ();
	}

}
