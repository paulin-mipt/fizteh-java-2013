package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.PrintStream;

/**
 * Created by Vladimir Mishatkin on 11/4/13
 */
public class ShellPrintStream {
	private PrintStream out;

	public ShellPrintStream() {
		out = null;
	}

	public ShellPrintStream(PrintStream out) {
		this.out = out;
	}

	public void print(String s) {
		if (out != null) {
			out.print(s);
		}
	}

	public void println(String s) {
		if (out != null) {
			out.println(s);
		}
	}
}
