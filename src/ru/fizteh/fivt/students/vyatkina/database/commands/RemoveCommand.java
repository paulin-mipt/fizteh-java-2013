package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class RemoveCommand extends DatabaseCommand {

    public RemoveCommand(DatabaseState state) {
        super(state);
        this.name = "remove";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        if (!tableIsSelected()) {
            return;
        }
        String key = args[0];

        String result = null;
        try {
            result = state.databaseAdapter.remove(key);
        }
        catch (WrappedIOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
        if (result == null) {
            state.printUserMessage("not found");
        } else {
            state.printUserMessage("removed");
        }
    }

}
