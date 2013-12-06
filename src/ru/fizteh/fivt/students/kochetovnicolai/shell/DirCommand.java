package ru.fizteh.fivt.students.kochetovnicolai.shell;

public class DirCommand extends Executable {

    private FileManager manager;

    public DirCommand(FileManager fileManager) {
        super("dir", 1);
        manager = fileManager;
    }

    @Override
    public boolean execute(String[] args) {
        String[] directories = manager.getCurrentPath().list();
        if (directories != null) {
            for (String directory : directories) {
                System.out.println(directory);
            }
        }
        return true;
    }
}
