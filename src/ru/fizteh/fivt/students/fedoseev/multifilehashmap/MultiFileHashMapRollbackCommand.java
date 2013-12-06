package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class MultiFileHashMapRollbackCommand extends AbstractCommand<State> {
    public MultiFileHashMapRollbackCommand() {
        super("rollback", 0);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        if (state.getCurTable() == null) {
            throw new IOException("no table");
        } else {
            System.out.println(state.rollback());
        }
    }
}
