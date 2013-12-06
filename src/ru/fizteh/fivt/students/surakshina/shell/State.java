package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;

public class State {
    protected File currentDirectory;
    protected boolean isInteractive;

    public State(File dir) {
        currentDirectory = dir;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(File newDirectory) {
        currentDirectory = newDirectory;
    }

    public void setMode(boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    public void printError(String message) {
        if (isInteractive) {
            System.out.println(message);
        } else {
            System.err.println(message);
            System.exit(1);
        }
    }
}
