package ru.fizteh.fivt.students.visamsonov.filehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandUse extends CommandAbstract<ShellState> {

	public CommandUse () {
		super("use");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		try {
			Table switchTable = state.tableProvider.getTable(args);
			if (switchTable == null) {
				getErrStream().println(args + " not exists");
				return false;
			}
			if (state.database != null) {
				state.database.commit();
			}
			state.database = switchTable;
		}
		catch (IllegalArgumentException e) {
			printError(e.getMessage());
			return false;
		}
		getOutStream().printf("using %s\n", args);
		return true;
	}
}