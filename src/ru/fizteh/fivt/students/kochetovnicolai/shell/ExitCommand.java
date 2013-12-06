package ru.fizteh.fivt.students.kochetovnicolai.shell;

public class ExitCommand extends Executable {

    private FileManager manager;

    public ExitCommand(FileManager fileManager) {
        super("exit", 1);
        manager = fileManager;
    }

    @Override
    public boolean execute(String[] args) {
        manager.setExit();
        return true;
    }
}
