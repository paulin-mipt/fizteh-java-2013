package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Command {
	public String getName();
	public int getArgsCount();
	public void execute(State state, String[] args) 
			throws FileNotFoundException, IOException;
	
}
