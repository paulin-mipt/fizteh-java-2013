package ru.fizteh.fivt.students.surakshina.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;

public class CommandRemove extends DataBaseCommand {
    public CommandRemove(TableState state) {
        super(state);
        name = "remove";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            String key = input[1];
            Storeable result;
            try {
                result = state.getTable().remove(key);
            } catch (IllegalArgumentException e) {
                state.printError(e.getMessage());
                return;
            }
            if (result != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }

    }
}
