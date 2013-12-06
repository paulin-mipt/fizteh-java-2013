package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class ShellCdCommand extends AbstractCommand<ShellState> {
    public ShellCdCommand() {
        super("cd", 1);
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        Path toCanPath = state.getCurState().toPath().resolve(input[0]).normalize();
        File newDir = new File(toCanPath.toString());

        if (!newDir.isDirectory()) {
            throw new FileNotFoundException("CD ERROR: not existing directory \"" + input[0]
                    + "\" in current directory");
        }

        state.setCurState(toCanPath.toFile());
    }
}
