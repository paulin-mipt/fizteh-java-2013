package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class CreateCommand extends DatabaseCommand {

    public CreateCommand(DatabaseState state) {
        super(state);
        this.name = "create";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        if (!saveChanges()) {
            return;
        }
        String tableName = args[0];
        boolean newTableIsCreated;
        try {
            newTableIsCreated = state.databaseAdapter.createTable(tableName);
        }
        catch (UnsupportedOperationException e) {
            state.printErrorMessage(e.getMessage());
            return;
        }
        catch (WrappedIOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
        if (newTableIsCreated) {
            state.printUserMessage("created");
        } else {
            state.printUserMessage(tableName + " exists");
        }
    }

}
