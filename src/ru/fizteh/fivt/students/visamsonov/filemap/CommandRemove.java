package ru.fizteh.fivt.students.visamsonov.filemap;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandRemove extends CommandAbstract<ShellState> {

	public CommandRemove () {
		super("remove");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		String value = state.database.remove(args);
		if (value != null) {
			getOutStream().printf("removed\n");
		}
		else {
			getOutStream().println("not found");
		}
		return true;
	}
}