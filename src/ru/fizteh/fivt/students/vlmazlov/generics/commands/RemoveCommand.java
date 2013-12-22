package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.OutputStream;

public class RemoveCommand extends AbstractDataBaseCommand {
    public RemoveCommand() {
        super("remove", 1);
    }

    public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
        if (state.getActiveTable() == null) {
            displayMessage("no table" + SEPARATOR, out);
            return;
        }

        String key = args[0];

        Object oldValue = null;
        try {
            oldValue = state.getActiveTable().remove(key);
        } catch (IllegalArgumentException ex) {
            displayMessage("operation failed: " + ex.getMessage() + SEPARATOR, out);
            return;
        }

        if (oldValue == null) {
            displayMessage("not found" + SEPARATOR, out);
        } else {
            displayMessage("removed" + SEPARATOR, out);
        }
    }
}
