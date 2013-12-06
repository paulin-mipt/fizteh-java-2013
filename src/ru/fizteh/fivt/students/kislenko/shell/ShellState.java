package ru.fizteh.fivt.students.kislenko.shell;

import java.nio.file.Path;

public class ShellState {
    private Path absolutePath;

    public Path getState() {
        return absolutePath;
    }

    public void setState(Path path) {
        absolutePath = path;
    }
}
