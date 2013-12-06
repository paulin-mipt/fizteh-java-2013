package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;

public class MkdirCommand extends AbstractCommand<ShellState> {
	public MkdirCommand() {
		super("mkdir", 1);
	}
	
	public void execute(String[] args, ShellState shellState) {
		shellState.fileCommander.createNewDirectory(args[1]);
	}
}
