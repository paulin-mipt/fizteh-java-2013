package ru.fizteh.fivt.students.vlmazlov.shell;

public class ShellState {
    private String currentDirectory;

    public ShellState(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public String getCurDir() {
        return currentDirectory;
    }

    //default access modifier used to let commands modify the shell's state, which is desirable for cd, for instance

    void changeCurDir(String newCurDir) {
        currentDirectory = newCurDir;
    }
}
