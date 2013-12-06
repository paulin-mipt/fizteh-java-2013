package ru.fizteh.fivt.students.asaitgalin.storable.commands;


import ru.fizteh.fivt.students.asaitgalin.shell.Command;
import ru.fizteh.fivt.students.asaitgalin.storable.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.storable.MultiFileTableUtils;

import java.io.IOException;

public class CreateCommand implements Command {
    private MultiFileTableState state;

    public CreateCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String[] parseCommandLine(String s) {
        // split by spaces except expressions in ()-brackets
        String[] args = s.split("\\s+(?![^\\(]*\\))");
        return args;
    }

    @Override
    public void execute(String[] args) throws IOException {
        // args[1] = tablename
        // args[2] = columns (...)
        String columnData = args[2].substring(1, args[2].length() - 1);
        String[] types = columnData.split("\\s");
        if (state.provider.createTable(args[1], MultiFileTableUtils.getColumnTypes(types)) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }

    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
