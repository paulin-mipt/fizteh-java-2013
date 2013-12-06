package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import java.io.IOException;

public class CpCommand extends AbstractCommand<ShellState> {
	public CpCommand() {
		super("cp", 2);
	}
	
	public void execute(String[] args, ShellState shellState) {
        try {
	        shellState.fileCommander.copyFiles(args[1], args[2]);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
	}
}
