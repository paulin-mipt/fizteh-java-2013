package ru.fizteh.fivt.students.asaitgalin.storable.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.storable.MultiFileTableState;

import java.io.IOException;
import java.text.ParseException;

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
            try {
                Storeable newValue = state.provider.deserialize(state.currentTable, args[2]);
                Storeable prev = state.currentTable.put(args[1], newValue);
                if (prev != null) {
                    System.out.println("overwrite");
                    System.out.println("old " + prev);
                } else {
                    System.out.println("new");
                }
            } catch (ParseException pe) {
                System.err.println("put: wrong input" + pe.getMessage());
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
