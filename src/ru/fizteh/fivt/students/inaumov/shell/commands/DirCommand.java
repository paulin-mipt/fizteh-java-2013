package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;

public class DirCommand extends AbstractCommand<ShellState> {
	public DirCommand() {
		super("dir", 0);
	}
	
	public void execute(String[] args, ShellState shellState) {
		String[] dirContent = shellState.fileCommander.getCurrentDirectoryContent();
        for (final String entry: dirContent) {
            System.out.println(entry);
        }
	}
}
