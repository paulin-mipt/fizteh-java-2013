package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class PutCommand extends DatabaseCommand {

    public PutCommand(DatabaseState state) {
        super(state);
        this.name = "put";
        this.argsCount = 2;
    }

    @Override
    public void execute(String[] args) {
        if (!tableIsSelected()) {
            return;
        }
        String key = args[0];
        String value = args[1];
        String oldValue;
        try {
            oldValue = state.databaseAdapter.put(key, value);
        }
        catch (IllegalArgumentException e) {
            state.printErrorMessage("Bad argument: " + e.getMessage());
            return;
        }
        catch (WrappedIOException e) {
            throw new CommandExecutionException(e.getMessage());
        }

        if (oldValue == null) {
            state.printUserMessage("new");
        } else {
            state.printUserMessage("overwrite");
            state.printUserMessage(oldValue);
        }
    }
}
