package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;

public class RollbackCommand extends AbstractCommand<SingleFileMapShellState> {
	public RollbackCommand() {
		super("rollback", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
        if (fileMapState.table == null) {
            throw new IllegalArgumentException("no table");
        }

        int unsavedChangesNumber = fileMapState.table.rollback();

		System.out.println(unsavedChangesNumber);
	}
}
