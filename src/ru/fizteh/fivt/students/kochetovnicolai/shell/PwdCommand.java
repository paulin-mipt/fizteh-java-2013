package ru.fizteh.fivt.students.kochetovnicolai.shell;

public class PwdCommand extends Executable {

    private FileManager manager;

    public PwdCommand(FileManager fileManager) {
        super("pwd", 1);
        manager = fileManager;
    }

    @Override
    public boolean execute(String[] args) {
        manager.printMessage(manager.getCurrentPath().getAbsolutePath());
        return true;
    }
}
