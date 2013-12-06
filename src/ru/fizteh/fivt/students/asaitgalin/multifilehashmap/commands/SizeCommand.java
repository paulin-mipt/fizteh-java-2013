package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class SizeCommand extends DefaultCommand {
    private MultiFileTableState state;

    public SizeCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.currentTable.size());
        }
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
