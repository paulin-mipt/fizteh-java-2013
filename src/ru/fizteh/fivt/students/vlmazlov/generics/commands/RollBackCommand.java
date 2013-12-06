package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.OutputStream;

public class RollBackCommand extends AbstractDataBaseCommand {
    public RollBackCommand() {
        super("rollback", 0);
    }

    public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
        if (state.getActiveTable() == null) {
            displayMessage("no table" + SEPARATOR, out);
            return;
        }

        displayMessage(state.getActiveTable().rollback() + SEPARATOR, out);
    }
}
