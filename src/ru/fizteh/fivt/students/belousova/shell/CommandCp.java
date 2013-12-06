package ru.fizteh.fivt.students.belousova.shell;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class CommandCp implements Command {
    private final String name = "cp";
    private ShellState state;

    public CommandCp(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 2;
    }

    public void execute(String[] args) throws IOException {

        String sourceName = args[1];
        String destinationName = args[2];

        File source = FileUtils.getFileFromString(sourceName, state);
        File destination = FileUtils.getFileFromString(destinationName, state);

        FileUtils.copy(source, destination);
    }
}
