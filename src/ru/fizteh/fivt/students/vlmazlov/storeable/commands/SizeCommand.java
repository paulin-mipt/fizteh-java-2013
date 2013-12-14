package ru.fizteh.fivt.students.vlmazlov.storeable.commands;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableDataBaseState;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;

import java.io.OutputStream;

public class SizeCommand extends AbstractDataBaseCommand {
    public SizeCommand() {
        super("size", 0);
    }

    public void execute(String[] args, StoreableDataBaseState state, OutputStream out) throws CommandFailException {
        if (state.getActiveTable() == null) {
            displayMessage("no table" + SEPARATOR, out);
            return;
        }

        displayMessage(state.getActiveTable().size() + SEPARATOR, out);
    }
}
