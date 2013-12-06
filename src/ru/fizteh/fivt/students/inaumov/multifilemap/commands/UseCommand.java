package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;
import ru.fizteh.fivt.storage.strings.Table;

public class UseCommand extends AbstractCommand<MultiFileMapShellState> {
    public UseCommand() {
        super("use", 1);
    }

    public void execute(String[] args, MultiFileMapShellState shellState) {
        Table oldTable = shellState.table;
        Table newTable = null;
        try {
            newTable = shellState.tableProvider.getTable(args[1]);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(args[1] + " not exists");
            return;
        }

        if (shellState.table != null) {
            shellState.table.commit();
        }

        shellState.table = newTable;

        System.out.println("using " + shellState.table.getName());
    }
}
