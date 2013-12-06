package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class MultiFileHashMapCommitCommand extends AbstractCommand<State> {
    public MultiFileHashMapCommitCommand() {
        super("commit", 0);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        if (state.getCurTable() == null) {
            throw new IOException("no table");
        } else {
            state.saveTable(state.getCurTable());

            System.out.println(state.commit());
        }
    }
}
