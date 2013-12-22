package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.OutputStream;

public class GetCommand extends AbstractDataBaseCommand {
    public GetCommand() {
        super("get", 1);
    }

    public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
        if (state.getActiveTable() == null) {
            displayMessage("no table" + SEPARATOR, out);
            return;
        }

        String key = args[0];
        Object value = null;
        try {
            value = state.getActiveTable().get(key);
        } catch (IllegalArgumentException ex) {
            displayMessage("operation failed: " + ex.getMessage() + SEPARATOR, out);
            return;
        }

        if (value == null) {
            displayMessage("not found" + SEPARATOR, out);
        } else {
            displayMessage("found" + SEPARATOR 
                + state.getProvider().serialize(state.getActiveTable(), value) + SEPARATOR, out);
        }
    }
}
