package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.OutputStream;

public class CreateCommand extends AbstractDataBaseCommand {
    public CreateCommand() {
        super("create", 1);
    }

    public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
        String tablename = args[0];

        if (state.getProvider().getTable(tablename) != null) {
            displayMessage(tablename + " exists" + SEPARATOR, out);
            return;
        }

        try {
            state.getProvider().createTable(tablename, null);
        } catch (IllegalArgumentException ex) {
            displayMessage("operation failed: " + ex.getMessage() + SEPARATOR, out);
            return;
        }

        displayMessage("created" + SEPARATOR, out);
    }
}
