package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;

public class ShellState {

    private File curDir;

    public File getCurDir() {
        return curDir;
    }

    public File changeCurDir(File newDir) {
        curDir = newDir;
        return curDir;
    }
}
