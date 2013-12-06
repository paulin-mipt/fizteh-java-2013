package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class FileMapRemoveCommand extends AbstractCommand<State> {
    public FileMapRemoveCommand() {
        super("remove", 1);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        if (state.usingTables()) {
            if (state.getCurTable() == null) {
                throw new IOException("no table");
            }
        }

        String removedValue = state.remove(input[0]);

        if (removedValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
