package ru.fizteh.fivt.students.visamsonov.junit;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandGet extends CommandAbstract<ShellState> {

	public CommandGet () {
		super("get");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		if (state.database == null) {
			printError("no table");
			return false;
		}
		String value = state.database.get(args);
		if (value != null) {
			getOutStream().printf("found\n%s\n", value);
		}
		else {
			getOutStream().println("not found");
		}
		return true;
	}
}