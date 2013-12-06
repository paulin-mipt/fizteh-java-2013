package ru.fizteh.fivt.students.surakshina.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;

public class CommandGet extends DataBaseCommand {

    public CommandGet(TableState stateNew) {
        super(stateNew);
        name = "get";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            String key = input[1];
            Storeable value = null;
            try {
                value = state.getTable().get(key);
            } catch (IllegalArgumentException e) {
                state.printError(e.getMessage());
                return;
            }
            if (value != null) {
                System.out.println("found");
                System.out.println(JSONSerializer.serialize(state.getTable(), value));
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }
    }
}
