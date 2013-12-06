package ru.fizteh.fivt.students.belousova.shell;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class CommandRm implements Command {
    private final String name = "rm";
    private ShellState state;

    public CommandRm(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 1;
    }

    public void execute(String[] args) throws IOException {

        String dirName = args[1];
        File directory = FileUtils.getFileFromString(dirName, state);

        if (directory.equals(state.getCurrentDirectory())) {
            throw new IOException("cannot remove '" + directory.getName() + "': it is a working directory");
        }

        if (directory.exists()) {
            FileUtils.deleteDirectory(directory);
        } else {
            throw new IOException("cannot remove " + dirName + ": No such file or directory");
        }
    }
}
