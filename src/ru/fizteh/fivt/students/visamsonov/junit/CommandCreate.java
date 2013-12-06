package ru.fizteh.fivt.students.visamsonov.junit;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandCreate extends CommandAbstract<ShellState> {

	public CommandCreate () {
		super("create");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		try {
			state.tableProvider.createTable(args);
		}
		catch (IllegalArgumentException e) {
			getErrStream().println(e.getMessage());
			return false;
		}
		getOutStream().println("created");
		return true;
	}
}