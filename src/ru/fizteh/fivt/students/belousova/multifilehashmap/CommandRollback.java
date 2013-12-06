package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandRollback implements Command {
    private MultiFileShellState state;

    public CommandRollback(MultiFileShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "rollback";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.rollbackCurrentTable());
        }
    }

    @Override
    public int getArgCount() {
        return 0;
    }
}
