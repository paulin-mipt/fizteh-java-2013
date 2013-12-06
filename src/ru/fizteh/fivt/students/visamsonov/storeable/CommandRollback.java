package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandRollback extends CommandAbstract<ShellState> {

	public CommandRollback () {
		super("rollback");
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 0)) {
			return false;
		}
		if (state.database == null) {
			printError("no table");
			return false;
		}
		getOutStream().println(state.database.rollback());
		return true;
	}
}