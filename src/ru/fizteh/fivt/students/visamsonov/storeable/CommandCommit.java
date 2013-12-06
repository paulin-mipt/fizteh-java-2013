package ru.fizteh.fivt.students.visamsonov.storeable;

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
		try {
			getOutStream().println(state.database.commit());
		}
		catch (IOException e) {
			printError(e.getMessage());
			return false;
		}
		return true;
	}
}