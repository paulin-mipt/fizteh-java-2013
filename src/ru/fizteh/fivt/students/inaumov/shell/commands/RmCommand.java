package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;

public class RmCommand extends AbstractCommand<ShellState> {
	public RmCommand() {
		super("rm", 1);
	}

	public void execute(String[] args, ShellState shellState) {
		shellState.fileCommander.remove(args[1]);
	}
}
