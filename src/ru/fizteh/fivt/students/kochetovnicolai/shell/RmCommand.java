package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class RmCommand extends Executable {

    private FileManager manager;

    public RmCommand(FileManager fileManager) {
        super("rm", 2);
        manager = fileManager;
    }

    @Override
    public boolean execute(String[] args) {
        File file = manager.resolvePath(args[1]);
        if (file == null) {
            manager.printMessage(args[0] + ": cannot remove \'" + args[1] + "\': No such file or directory");
            return false;
        }
        if (file.isDirectory() && manager.getCurrentPath().getAbsolutePath().contains(file.getAbsolutePath())) {
            manager.printMessage(args[0] + ": cannot remove \'" + args[1] + "\': cannot delete current directory");
            return false;
        }
        return manager.recursiveRemove(file, args[0]);
    }
}
