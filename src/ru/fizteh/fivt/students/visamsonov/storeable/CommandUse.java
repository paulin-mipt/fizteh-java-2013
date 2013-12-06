package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;
import ru.fizteh.fivt.students.visamsonov.storage.*;
import ru.fizteh.fivt.storage.strings.Table;

public class CommandUse extends CommandAbstract<ShellState> {

	public CommandUse () {
		super("use");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		try {
			StructuredTableInterface switchTable = state.tableProvider.getTable(args);
			if (switchTable == null) {
				getErrStream().println(args + " not exists");
				return false;
			}
			if (state.database != null && state.database.unsavedChanges() > 0) {
				getErrStream().println(state.database.unsavedChanges() + " unsaved changes");
				return false;
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