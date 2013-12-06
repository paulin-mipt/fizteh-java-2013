package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.nio.file.Path;

public class ShellState {

    private Path path;

    public ShellState(File inFile) {

        path = inFile.getAbsoluteFile().toPath();
    }

    public Path getPath() {

        return path;
    }

    public void setPath(Path inPath) {

        path = inPath;
    }
}