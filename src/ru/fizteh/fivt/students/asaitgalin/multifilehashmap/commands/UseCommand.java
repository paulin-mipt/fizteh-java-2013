package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTable;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class UseCommand extends DefaultCommand {
    private MultiFileTableState state;

    public UseCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.currentTable != null) {
            int changes = state.currentTable.getChangesCount();
            if (changes != 0) {
                System.out.println(changes + " unsaved changes");
            } else {
                changeTable(args[1]);
            }
        } else {
            changeTable(args[1]);
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    private void changeTable(String name) {
        ChangesCountingTable table = state.provider.getTable(name);
        if (table != null) {
            state.currentTable = table;
            System.out.println("using " + name);
        } else {
            System.out.println(name + " not exists");
        }
    }

}
