package ru.fizteh.fivt.students.abramova.shell;

public class Stage {
    private String pathToCurrentDir;

    public Stage(String absPath) {
        pathToCurrentDir = absPath;
    }
    public Stage setStage(String absPath) {
        pathToCurrentDir = absPath;
        return this;
    }
    public String currentDirPath() {
        return pathToCurrentDir;
    }
}
