package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandSize extends CommandAbstract<ShellState> {

	public CommandSize () {
		super("size");
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 0)) {
			return false;
		}
		if (state.database == null) {
			printError("no table");
			return false;
		}
		getOutStream().println(state.database.size());
		return true;
	}
}