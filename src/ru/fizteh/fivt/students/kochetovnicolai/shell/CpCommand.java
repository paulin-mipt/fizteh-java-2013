package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class CpCommand extends Executable {

    private FileManager manager;

    public CpCommand(FileManager fileManager) {
        super("cp", 3);
        manager = fileManager;
    }

    @Override
    public boolean execute(String[] args) {
        File source = manager.resolvePath(args[1]);
        File destination = manager.resolvePath(args[2]);
        return manager.safeCopy(source, destination, getName());
    }
}
