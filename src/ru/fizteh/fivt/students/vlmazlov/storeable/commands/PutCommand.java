package ru.fizteh.fivt.students.vlmazlov.storeable.commands;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableDataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.OutputStream;
import java.text.ParseException;

public class PutCommand extends AbstractDataBaseCommand {
    public PutCommand() {
        super("put", 2);
    }

    public void execute(String[] args, StoreableDataBaseState state, OutputStream out) throws CommandFailException {
        if (state.getActiveTable() == null) {
            displayMessage("no table" + SEPARATOR, out);
            return;
        }

        String key = args[0];
        String value = args[1];
        Storeable oldValue = null;

        try {
            oldValue = state.getActiveTable().put(key, state.getProvider().deserialize(state.getActiveTable(), value));
        } catch (ParseException ex) {
            displayMessage("wrong type("
                    + value + " cannot be deserialized as a row for table " + state.getActiveTable().getName()
                    + ": " + ex.getMessage() + ")" + SEPARATOR, out);
            return;
        } catch (IllegalArgumentException ex) {
            displayMessage("operation failed: " + ex.getMessage() + SEPARATOR, out);
            return;
        }

        if (oldValue == null) {
            displayMessage("new" + SEPARATOR, out);
        } else {
            displayMessage("overwrite" + SEPARATOR 
                + state.getProvider().serialize(state.getActiveTable(), oldValue) + SEPARATOR, out);
        }
    }
}
