package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class UseCommand extends DatabaseCommand {

    public UseCommand(DatabaseState state) {
        super(state);
        this.name = "use";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        if (!saveChanges()) {
            return;
        }
        String tableName = args[0];
        boolean tableExists;
        try {
            tableExists = state.databaseAdapter.useTable(tableName);
        }
        catch (UnsupportedOperationException e) {
            state.printErrorMessage(e.getMessage());
            return;
        }
        catch (WrappedIOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
        if (tableExists) {
            state.printUserMessage("using " + tableName);
        } else {
            state.printUserMessage(tableName + " not exists");
        }
    }
}


