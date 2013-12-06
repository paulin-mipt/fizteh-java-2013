package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;

public class DropCommand extends AbstractCommand<MultiFileMapShellState> {
    public DropCommand() {
        super("drop", 1);
    }

    public void execute(String[] args, MultiFileMapShellState shellState) {
        try {
            shellState.tableProvider.removeTable(args[1]);
            shellState.table = null;
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
