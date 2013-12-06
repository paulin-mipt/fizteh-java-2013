package ru.fizteh.fivt.students.inaumov.shell.exceptions;

public class UserInterruptionException extends Exception {
	public UserInterruptionException() {
		super();
	}

	public UserInterruptionException(String exceptionMessage) {
		super(exceptionMessage);
	}
}
