package ru.fizteh.fivt.students.vyatkina.database.commands;


import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class SizeCommand extends DatabaseCommand {

    public SizeCommand(DatabaseState state) {
        super(state);
        this.name = "size";
        this.argsCount = 0;
    }

    @Override
    public void execute(String[] args) {
        if (!tableIsSelected()) {
            return;
        }
        int tableSize;
        try {
            tableSize = state.databaseAdapter.size();
        }
        catch (UnsupportedOperationException e) {
            state.printErrorMessage(e.getMessage());
            return;
        }
        state.printUserMessage(String.valueOf(tableSize));
    }
}
