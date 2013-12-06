package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class MultiFileHashMapSizeCommand extends AbstractCommand<State> {
    public MultiFileHashMapSizeCommand() {
        super("size", 0);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        if (state.getCurTable() == null) {
            throw new IOException("no table");
        } else {
            System.out.println(state.size());
        }
    }
}
