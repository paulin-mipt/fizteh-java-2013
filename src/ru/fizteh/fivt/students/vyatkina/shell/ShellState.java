package ru.fizteh.fivt.students.vyatkina.shell;

import ru.fizteh.fivt.students.vyatkina.FileManager;
import ru.fizteh.fivt.students.vyatkina.State;

public class ShellState extends State {

    private FileManager fileManager;

    public ShellState(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

}
