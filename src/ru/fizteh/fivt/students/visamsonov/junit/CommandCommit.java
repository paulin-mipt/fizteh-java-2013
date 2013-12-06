package ru.fizteh.fivt.students.visamsonov.junit;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

import java.io.*;

public class CommandCommit extends CommandAbstract<ShellState> {

	public CommandCommit () {
		super("commit");
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 0)) {
			return false;
		}
		if (state.database == null) {
			printError("no table");
			return false;
		}
		getOutStream().println(state.database.commit());
		return true;
	}
}