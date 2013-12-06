package ru.fizteh.fivt.students.surakshina.filemap;

import ru.fizteh.fivt.storage.structured.Table;

public class CommandUse extends DataBaseCommand {
    public CommandUse(TableState state) {
        super(state);
        name = "use";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        String name = input[1];
        int count = 0;
        Table table = null;
        try {
            if (state.getTable() != null) {
                count = state.getTable().unsavedChanges();
                if (count != 0) {
                    System.out.println(count + " unsaved changes");
                    return;
                }
            }
            table = state.getTableProvider().getTable(name);
            if (table != null) {
                state.getTableProvider().setCurrentTable((NewTable) table);
                System.out.println("using " + name);
            } else {
                System.out.println(name + " not exists");
                return;
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            state.printError(e.getMessage());
            return;
        }
    }
}
