package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;

public class ShellMkdirCommand extends AbstractCommand<ShellState> {
    public ShellMkdirCommand() {
        super("mkdir", 1);
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        File newDir = new File(state.getCurState().toPath().resolve(input[0]).toString());

        if (!newDir.mkdirs()) {
            throw new IOException("MKDIR ERROR: directory \"" + input[0]
                    + "\" already exists or some other error has happened");
        }
    }
}
