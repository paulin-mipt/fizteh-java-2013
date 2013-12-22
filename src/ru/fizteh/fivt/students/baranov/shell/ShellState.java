package ru.fizteh.fivt.students.baranov.shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ShellState {
    private Path currentPath;

    ShellState(Path path) {
        this.currentPath = path;
    }

    public Path getCurrentPath() {
        return currentPath.toAbsolutePath();
    }

    public void changeCurrentPath(Path newPath) {
        currentPath = newPath;
    }
}
