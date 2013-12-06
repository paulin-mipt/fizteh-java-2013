package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;
import ru.fizteh.fivt.storage.structured.Storeable;

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
		Storeable value = state.database.get(args);
		if (value != null) {
			getOutStream().printf("found\n%s\n", state.tableProvider.serialize(state.database, value));
		}
		else {
			getOutStream().println("not found");
		}
		return true;
	}
}