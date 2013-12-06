package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class FileMapExitCommand extends AbstractCommand<State> {
    public FileMapExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, State state) throws IOException, InterruptedException {
        if (state.usingTables()) {
            state.saveTable(state.getCurTable());
        } else {
            state.saveTable(null);
        }

        Thread.currentThread().interrupt();
    }
}
