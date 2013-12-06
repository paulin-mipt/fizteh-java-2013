package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.PrintStream;

interface Command<T> {

	public void printError (String message);

	public String getName ();
	
	public boolean evaluate (T state, String args);
}