package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class RollbackCommand extends DatabaseCommand {

    public RollbackCommand(DatabaseState state) {
        super(state);
        this.name = "rollback";
        this.argsCount = 0;
    }

    @Override
    public void execute(String[] args) {
        if (!tableIsSelected()) {
            return;
        }
        int deletedChanges = 0;
        try {
            deletedChanges = state.databaseAdapter.rollback();
        }
        catch (UnsupportedOperationException e) {
            state.printErrorMessage(e.getMessage());
            return;
        }
        state.printUserMessage(String.valueOf(deletedChanges));
    }
}
