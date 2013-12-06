package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class RemoveCommand extends DefaultCommand {
    MultiFileTableState state;

    public RemoveCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.currentTable == null) {
            System.out.println("no table");
        } else {
            String value = state.currentTable.remove(args[1]);
            if (value != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
