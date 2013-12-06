package ru.fizteh.fivt.students.vyatkina.database.commands;


import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class CommitCommand extends DatabaseCommand {

    public CommitCommand(DatabaseState state) {
        super(state);
        this.name = "commit";
        this.argsCount = 0;

    }

    @Override
    public void execute(String[] args) throws CommandExecutionException {
        if (!tableIsSelected()) {
            return;
        }
        int commitedChanges = 0;
        try {
            commitedChanges = state.databaseAdapter.commit();
        }
        catch (UnsupportedOperationException e) {
            state.printErrorMessage(e.getMessage());
            return;
        }
        catch (WrappedIOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
        state.printUserMessage(String.valueOf(commitedChanges));
    }

}
