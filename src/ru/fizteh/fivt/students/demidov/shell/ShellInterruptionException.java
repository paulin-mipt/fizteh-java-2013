package ru.fizteh.fivt.students.demidov.shell;

public class ShellInterruptionException extends RuntimeException {
	public ShellInterruptionException() { 
		super(); 
	}
	public ShellInterruptionException(String message) { 
		super(message); 
	}
	public ShellInterruptionException(Throwable gotException) { 
		super(gotException); 
	}
	public ShellInterruptionException(String message, Throwable gotException) { 
		super(message, gotException); 
	}
}
