package ru.fizteh.fivt.students.belousova.shell;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class CommandMv implements Command {
    private final String name = "mv";
    private ShellState state;

    public CommandMv(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 2;
    }

    public void execute(String[] args) throws IOException {

        String srcName = args[1];
        String dstName = args[2];

        File source = FileUtils.getFileFromString(srcName, state);
        File destination = FileUtils.getFileFromString(dstName, state);

        if (source.equals(state.getCurrentDirectory())) {
            throw new IOException("cannot move '" + source.getName() + "': it is a working directory");
        }

        FileUtils.move(source, destination);
    }
}
