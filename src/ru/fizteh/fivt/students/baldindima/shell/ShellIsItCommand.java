package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellIsItCommand implements ShellCommand{
	protected String name;
	protected String[] arguments;
	protected int numberOfArgs;
	public void run() throws IOException{
	}
	public boolean isItCommand(final String[] commands) throws IOException{
		if (commands[0].equals(name)){
		if (commands.length != numberOfArgs){
			throw new IOException("Invalid number of arguments");
		
		}
		arguments = commands;
		return true;
		}
		return false;
	}
	public void setName(final String newName) {
        name = newName;
    }

    public void setNumberOfArgs(final int newNumberOfArgs) {
        numberOfArgs = newNumberOfArgs;
    }

	

}
