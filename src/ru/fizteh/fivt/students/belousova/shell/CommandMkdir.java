package ru.fizteh.fivt.students.belousova.shell;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class CommandMkdir implements Command {
    private static final String name = "mkdir";
    private ShellState state;

    public CommandMkdir(ShellState state) {
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
        File newDirectory = FileUtils.getFileFromString(dirName, state);

        if (!newDirectory.getParentFile().exists()) {
            throw new IOException("parent directory '" + newDirectory.getParent() + "' doesn't exist");
        }

        boolean success = newDirectory.mkdir();
        if (!success) {
            throw new IOException("cannot create '" + dirName + "': directory already exists");
        }
    }
}
