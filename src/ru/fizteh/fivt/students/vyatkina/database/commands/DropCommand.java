package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class DropCommand extends DatabaseCommand {

    public DropCommand(DatabaseState state) {
        super(state);
        this.name = "drop";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        String tableName = args[0];
        boolean tableIsDropped;
        try {
            tableIsDropped = state.databaseAdapter.dropTable(tableName);
        }
        catch (UnsupportedOperationException e) {
            state.printErrorMessage(e.getMessage());
            return;
        }
        catch (WrappedIOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
        if (tableIsDropped) {
            state.printUserMessage("dropped");
        } else {
            state.printUserMessage(tableName + " not exists");
        }
    }

}
