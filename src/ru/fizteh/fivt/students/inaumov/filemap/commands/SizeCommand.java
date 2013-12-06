package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;

public class SizeCommand extends AbstractCommand<SingleFileMapShellState> {
	public SizeCommand() {
		super("size", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
        if (fileMapState.table == null) {
            throw new IllegalArgumentException("no table");
        }

        System.out.println(fileMapState.table.size());
	}
}
