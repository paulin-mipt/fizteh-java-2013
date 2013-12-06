package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandUse implements Command {
    MultiFileShellState state = null;

    public CommandUse(MultiFileShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public void execute(String[] args) throws IOException {
        String tableName = args[1];
        if (!state.getTable(tableName)) {
            System.out.println(tableName + " not exists");
        } else {
            if (state.getCurrentTable() != null) {
                if (state.getChangesCountOfCurrentTable() > 0) {
                    System.out.println(state.getChangesCountOfCurrentTable() + " unsaved changes");
                    return;
                }
            }
            state.setCurrentTable(tableName);
            System.out.println("using " + tableName);
        }
    }

    @Override
    public int getArgCount() {
        return 1;
    }
}
