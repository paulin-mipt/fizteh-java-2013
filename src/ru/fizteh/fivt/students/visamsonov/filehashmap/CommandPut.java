package ru.fizteh.fivt.students.visamsonov.filehashmap;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandPut extends CommandAbstract<ShellState> {

	public CommandPut () {
		super("put");
	}
	
	public boolean evaluate (ShellState state, String args) {
		String[] argArray = splitArguments(args);
		if (!checkFixedArguments(argArray, 2)) {
			return false;
		}
		if (state.database == null) {
			printError("no table");
			return false;
		}
		String oldValue = state.database.get(argArray[0]);
		state.database.put(argArray[0], argArray[1]);
		if (oldValue != null) {
			getOutStream().printf("overwrite\n%s\n", oldValue);
		}
		else {
			getOutStream().println("new");
		}
		return true;
	}
}