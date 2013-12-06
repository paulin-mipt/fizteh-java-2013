package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class PutCommand extends DefaultCommand {
    private MultiFileTableState state;

    public PutCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "put";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.currentTable == null) {
            System.out.println("no table");
        } else {
            String prev = state.currentTable.put(args[1], args[2]);
            if (prev != null) {
                System.out.println("overwrite");
                System.out.println("old " + prev);
            } else {
                System.out.println("new");
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
