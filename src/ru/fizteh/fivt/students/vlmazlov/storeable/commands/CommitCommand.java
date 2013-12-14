package ru.fizteh.fivt.students.vlmazlov.storeable.commands;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableDataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.IOException;
import java.io.OutputStream;

public class CommitCommand extends AbstractDataBaseCommand {
    public CommitCommand() {
        super("commit", 0);
    }

    public void execute(String[] args, StoreableDataBaseState state, OutputStream out) throws CommandFailException {
        if (state.getActiveTable() == null) {
            displayMessage("no table" + SEPARATOR, out);
            return;
        }
        try {
            displayMessage(state.getActiveTable().commit() + SEPARATOR, out);
        } catch (IOException ex) {
            throw new CommandFailException("Commit failed: " + ex.getMessage());
        }
    }
}
