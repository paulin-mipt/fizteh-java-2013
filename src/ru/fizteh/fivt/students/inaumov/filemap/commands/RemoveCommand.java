package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;

public class RemoveCommand extends AbstractCommand<SingleFileMapShellState> {
	public RemoveCommand() {
		super("remove", 1);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
        if (fileMapState.table == null) {
            throw new IllegalArgumentException("no table");
        }

        String oldValue = fileMapState.table.remove(args[1]);
		if (oldValue == null) {
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
	}
}
