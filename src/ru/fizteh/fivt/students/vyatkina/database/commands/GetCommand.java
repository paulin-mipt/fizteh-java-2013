package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

public class GetCommand extends DatabaseCommand {

    public GetCommand(DatabaseState state) {
        super(state);
        this.name = "get";
        this.argsCount = 1;
    }

    @Override
    public void execute(String[] args) {
        if (!tableIsSelected()) {
            return;
        }
        String key = args[0];
        String result = state.databaseAdapter.get(key);
        if (result == null) {
            state.printUserMessage("not found");
        } else {
            state.printUserMessage("found");
            state.printUserMessage(result);
        }
    }

}
