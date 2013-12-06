package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;

public class ShellState {
    private File curDir;

    public void setCurState(File file) {
        curDir = file;
    }

    public File getCurState() {
        return curDir;
    }
}
