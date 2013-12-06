package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.TimeToFinishException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class ExitDatabaseCommand extends DatabaseCommand {

    public ExitDatabaseCommand(DatabaseState state) {
        super(state);
        this.name = "exit";
        this.argsCount = 0;
    }

    @Override
    public void execute(String[] args) {
        try {
            state.databaseAdapter.saveChangesOnExit();
        }
        catch (WrappedIOException e) {
            state.printErrorMessage(e.getMessage());
        }
        finally {
            throw new TimeToFinishException();
        }
    }
}
