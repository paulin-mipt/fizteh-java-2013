package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * TimeToExitException.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class TimeToExitException extends ShellException {

	public TimeToExitException(String s) {
		super(s);
	}

	public TimeToExitException() {
		super("");
	}

}
