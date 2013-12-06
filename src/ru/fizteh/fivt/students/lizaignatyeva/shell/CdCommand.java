package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;

public class CdCommand extends Command {
    public CdCommand() {
        name = "cd";
        argumentsAmount = 1;
    }

    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        File path = Shell.getPath();
        String pathName = args[0];
        File newPath = new File(pathName);
        if (!newPath.isAbsolute()) {
            newPath = new File(path.getCanonicalPath() + File.separator + pathName);
        }
        if (!newPath.isDirectory()) {
            throw new IllegalArgumentException(pathName + ": No such file or directory");
        } else {
            path = newPath;
        }
        path = new File(path.getCanonicalPath());
        Shell.setPath(path);
    }
}
