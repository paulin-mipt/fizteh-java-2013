package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class CommandUse implements Command<MultiTableFatherState> {

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public int getArgCount() {
        return 1;
    }

    @Override
    public void run(MultiTableFatherState state, String[] args) throws Exception {
        if (!state.needToChangeTable(args[0])) {
            System.out.println("using " + args[0]);
            return;
        }
        AtomicReference<String> message = new AtomicReference<String>();
        if (state.isTransactional() && state.getTableChangeCount() != 0) {
            System.out.println(state.getTableChangeCount() + " unsaved changes");
            throw new IOException("Unsaved changes detected.");
        }
        if (state.autoCommit()) {
            state.commitCurrentTable();
        }
        state.dumpOldTable();
        state.changeTable(args[0], message);
        System.out.println(message);
    }
}
