package ru.fizteh.fivt.students.vlmazlov.storeable.commands;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableDataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.OutputStream;

public class DropCommand extends AbstractDataBaseCommand {
    public DropCommand() {
        super("drop", 1);
    }

    public void execute(String[] args, StoreableDataBaseState state, OutputStream out) throws CommandFailException {
        String tablename = args[0];

        if (state.getProvider().getTable(tablename) == null) {
            displayMessage(tablename + " not exists" + SEPARATOR, out);
            return;
        }

        if (state.getProvider().getTable(tablename) == state.getActiveTable()) {
            state.setActiveTable(null);
        }

        state.getProvider().removeTable(tablename);

        displayMessage("dropped" + SEPARATOR, out);
    }
}
