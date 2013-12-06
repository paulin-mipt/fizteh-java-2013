package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.IOException;

/**
 * Created by Vladimir Mishatkin on 10/1/13
 */
public class ShellException extends IOException {
	public ShellException(String s) {
		super(s);
	}

	public ShellException() {
		super();
	}

	public ShellException(String message, Throwable cause) {
		super(message, cause);
	}

	public ShellException(Throwable cause) {
		super(cause);
	}
}
