package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class RollbackCommand extends DefaultCommand {
    private MultiFileTableState state;

    public RollbackCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "rollback";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.currentTable.rollback());
        }
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
