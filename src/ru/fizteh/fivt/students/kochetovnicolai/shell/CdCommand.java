package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class CdCommand extends Executable {

    private FileManager manager;

    public CdCommand(FileManager fileManager) {
        super("cd", 2);
        manager = fileManager;
    }

    @Override
    public boolean execute(String[] args) {
        File newPath = manager.resolvePath(args[1]);
        if (newPath == null || !newPath.exists()) {
            manager.printMessage(args[0] + ": \'" + args[1] + "\': No such file or directory");
        } else if (!newPath.isDirectory()) {
            manager.printMessage(args[0] + ": \'" + args[1] + "': expected directory name, but file found");
        } else {
            return manager.setCurrentPath(newPath);
        }
        return false;
    }
}
