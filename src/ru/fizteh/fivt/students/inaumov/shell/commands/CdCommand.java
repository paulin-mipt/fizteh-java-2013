package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;

public class CdCommand extends AbstractCommand<ShellState> {
	public CdCommand() {
		super("cd", 1);
	}
	
	public void execute(String[] args, ShellState shellState) {
		String newPath = args[1];
		shellState.fileCommander.setCurrentDirectory(newPath);
	}
}
