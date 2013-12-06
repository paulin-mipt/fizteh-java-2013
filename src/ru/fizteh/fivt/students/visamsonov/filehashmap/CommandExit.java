package ru.fizteh.fivt.students.visamsonov.filehashmap;

import java.io.*;
import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;

public class CommandExit extends CommandAbstract<ShellState> {

	public CommandExit () {
		super("exit");
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 0)) {
			return false;
		}
		if (state.database != null) {
			state.database.commit();
		}
		System.exit(0);
		return true;
	}
}