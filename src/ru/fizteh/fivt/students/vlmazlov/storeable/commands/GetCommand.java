package ru.fizteh.fivt.students.vlmazlov.storeable.commands;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableDataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.OutputStream;

public class GetCommand extends AbstractDataBaseCommand {
    public GetCommand() {
        super("get", 1);
    }

    public void execute(String[] args, StoreableDataBaseState state, OutputStream out) throws CommandFailException {
        if (state.getActiveTable() == null) {
            displayMessage("no table" + SEPARATOR, out);
            return;
        }

        String key = args[0];
        Storeable value = null;
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
