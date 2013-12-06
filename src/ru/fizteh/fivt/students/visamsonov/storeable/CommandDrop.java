package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;
import java.io.IOException;

public class CommandDrop extends CommandAbstract<ShellState> {

	public CommandDrop () {
		super("drop");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		if (state.database != null && state.database.getName().equals(args)) {
			state.database = null;
		}
		try {
			state.tableProvider.removeTable(args);
		}
		catch (IllegalArgumentException | IOException e) {
			printError(e.getMessage());
			return false;
		}
		catch (IllegalStateException e) {
			getErrStream().println(e.getMessage());
			return false;
		}
		getOutStream().println("dropped");
		return true;
	}
}