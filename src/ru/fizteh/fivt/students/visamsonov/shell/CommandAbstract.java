package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.PrintStream;

public abstract class CommandAbstract<T> implements Command<T> {

	protected String name;
	private PrintStream errStream;
	private PrintStream outStream;

	public CommandAbstract (String commandName) {
		name = commandName;
		errStream = System.err;
		outStream = System.out;
	}

	public CommandAbstract (String commandName, PrintStream out, PrintStream err) {
		name = commandName;
		redirectStreams(out, err);
	}

	public void redirectStreams (PrintStream out, PrintStream err) {
		errStream = err;
		outStream = out;
	}

	public PrintStream getOutStream () {
		return outStream;
	}

	public PrintStream getErrStream () {
		return errStream;
	}

	public void printError (String message) {
		errStream.printf("%s: %s\n", name, message);
	}

	protected String[] splitArguments (String args) {
		if (args == null || args.equals("")) {
			return new String[0];
		}
		return args.split("\\s+");
	}

	protected boolean checkFixedArguments (String[] args, int number) {
		if (args == null || args.length != number) {
			printError("given " + args.length + " arguments, expected " + number);
			return false;
		}
		return true;
	}

	public String getName () {
		return name;
	}
}