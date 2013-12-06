package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;

public class DirCommand extends Command {
    public DirCommand() {
        name = "dir";
        argumentsAmount = 0;
    }

    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        File path = Shell.getPath();
        if (!path.exists()) {
            throw new IllegalArgumentException("Current path does not exist");
        }
        for (String child : path.list()) {
            System.out.println(child);
        }

    }
}
