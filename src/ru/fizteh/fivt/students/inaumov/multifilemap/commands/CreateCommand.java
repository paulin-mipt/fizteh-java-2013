package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;

public class CreateCommand extends AbstractCommand<MultiFileMapShellState> {
    public CreateCommand() {
        super("create", 1);
    }

    public void execute(String[] args, MultiFileMapShellState shellState) {
        Table newTable = shellState.tableProvider.createTable(args[1]);
        if (newTable == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }
}
