package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class CreateCommand extends DefaultCommand {
    private MultiFileTableState state;

    public CreateCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.provider.createTable(args[1]) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
