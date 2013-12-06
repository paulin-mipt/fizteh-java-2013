package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import java.io.IOException;

public class MvCommand extends AbstractCommand<ShellState> {
	public MvCommand() {
		super("mv", 2);
	}

	public void execute(String[] args, ShellState shellState) {
        try {
		    shellState.fileCommander.moveFiles(args[1], args[2]);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
	}
}
