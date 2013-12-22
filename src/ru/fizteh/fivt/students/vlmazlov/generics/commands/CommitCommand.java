package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.IOException;
import java.io.OutputStream;

public class CommitCommand extends AbstractDataBaseCommand {
    public CommitCommand() {
        super("commit", 0);
    }

    public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
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
