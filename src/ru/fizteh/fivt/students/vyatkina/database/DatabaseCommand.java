package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;

abstract public class DatabaseCommand extends AbstractCommand<DatabaseState> {

    public DatabaseCommand(DatabaseState state) {
        super(state);
    }

    protected boolean tableIsSelected() {
        if (!state.databaseAdapter.tableIsSelected()) {
            state.printUserMessage("no table");
            return false;
        } else {
            return true;
        }
    }

    protected boolean saveChanges() {
        int unsavedChanges = state.databaseAdapter.unsavedChanges();
        if (unsavedChanges == 0) {
            return true;
        } else {
            state.printUserMessage(unsavedChanges + " unsaved changes");
            return false;
        }
    }

}
