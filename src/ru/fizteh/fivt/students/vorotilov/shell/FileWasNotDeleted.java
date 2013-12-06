package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.File;

public class FileWasNotDeleted extends Exception {

    protected File problematicFile;

    FileWasNotDeleted(File pF) {
        super();
        problematicFile = pF;
    }

    public File getProblematicFile() {
        return problematicFile;
    }
}
