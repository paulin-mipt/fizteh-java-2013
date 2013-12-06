package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.io.File;
import java.io.IOException;

public abstract class State {
    private File currentDirectory;

    public State() {
        currentDirectory = new File(".");
    }

    public File compileFile(String path) {
        File tempFile = new File(path);
        if (!tempFile.isAbsolute()) {
            tempFile = new File(currentDirectory, path);
        }
        return tempFile;
    }

    public String getCurrentDirectory() throws CommandException {
        String currentDirectoryStr = "";
        try {
            currentDirectoryStr = currentDirectory.getCanonicalPath();
        } catch (IOException e) {
            throw new CommandException("Can't get path to current directory");
        }
        return currentDirectoryStr;
    }

    public void changeCurrentDirectory(String newDir) throws CommandException {
        File tempDir = compileFile(newDir);
        if (tempDir.exists() && tempDir.isDirectory()) {
            currentDirectory = tempDir;
        } else {
            throw new CommandException(newDir + ": no such directory");
        }
    }

}

