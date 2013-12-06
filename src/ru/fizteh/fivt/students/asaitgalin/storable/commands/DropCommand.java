package ru.fizteh.fivt.students.asaitgalin.storable.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.storable.MultiFileTableState;

import java.io.IOException;

public class DropCommand extends DefaultCommand {
    private MultiFileTableState state;

    public DropCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public void execute(String[] args) throws IOException {
        try {
            state.provider.removeTable(args[1]);
            if (state.currentTable != null && args[1].equals(state.currentTable.getName())) {
                state.currentTable = null;
            }
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(args[1] + " not exists");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
