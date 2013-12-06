package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;

public class PwdCommand extends AbstractCommand<ShellState> {
	public PwdCommand() {
		super("pwd", 0);
	}

	public void execute(String[] args, ShellState shellState) {
        String workingDir = shellState.fileCommander.getCurrentDirectory();
        System.out.println(workingDir);
	}
}
