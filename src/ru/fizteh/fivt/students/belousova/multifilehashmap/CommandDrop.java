package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandDrop implements Command {
    MultiFileShellState state = null;

    public CommandDrop(MultiFileShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public void execute(String[] args) throws IOException {
        String key = args[1];
        if (!state.getTable(key)) {
            System.out.println(key + " not exists");
        } else {
            if (state.getCurrentTable() != null && state.getCurrentTable().equals(key)) {
                state.resetCurrentTable();
            }
            System.out.println("dropped");
            state.removeTable(key);
        }
    }

    @Override
    public int getArgCount() {
        return 1;
    }
}
