package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;

public class CommitCommand extends AbstractCommand<SingleFileMapShellState> {
	public CommitCommand() {
		super("commit", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
        if (fileMapState.table == null) {
            throw new IllegalArgumentException("no table");
        }

		int savedChangesNumber = fileMapState.table.commit();

		System.out.println(savedChangesNumber);
	}
}
