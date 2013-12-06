package ru.fizteh.fivt.students.belousova.shell;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class ShellState {

    private File currentDirectory;

    public ShellState() {
        currentDirectory = new File("").getAbsoluteFile();
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String directory) throws IOException {
        File newDirectory = FileUtils.getFileFromString(directory, this);
        if (newDirectory.exists() && newDirectory.isDirectory()) {
            currentDirectory = newDirectory.getCanonicalFile();
        } else {
            throw new IOException("'" + directory + "': No such file or directory");
        }
    }
}
