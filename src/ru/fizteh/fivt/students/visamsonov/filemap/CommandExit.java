package ru.fizteh.fivt.students.visamsonov.filemap;

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
		state.database.commit();
		System.exit(0);
		return true;
	}
}