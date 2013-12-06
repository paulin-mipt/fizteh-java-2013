package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.File;

public abstract class State {
    public String workingDirectory;

    public State() {
        File file = new File("");
        workingDirectory = file.getAbsolutePath();
    }

    public void printWorkDir() {
        System.out.println(workingDirectory);
    }
}
