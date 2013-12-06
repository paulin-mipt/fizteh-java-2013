package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class MvCommand extends Executable {

    private FileManager manager;

    public MvCommand(FileManager fileManager) {
        super("mv", 3);
        manager = fileManager;
    }

    @Override
    public boolean execute(String[] args) {
        File source = manager.resolvePath(args[1]);
        File destination = manager.resolvePath(args[2]);
        return manager.safeCopy(source, destination, getName()) && manager.recursiveRemove(source, getName());
    }
}
