package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.File;

class DirCommand extends ExternalCommand {
    public DirCommand() {
        super("dir", 0);
    }

    public void execute(String[] args, Shell shell) {
        File dir = Utilities.getAbsoluteFile("", shell.getCurrentPath());

        if (!dir.exists()) {
            throw new IllegalArgumentException("dir: dir of a non-existing directory.");
        }

        File[] fList = dir.listFiles();

        for (File f: fList) {
            System.out.println(f.getName());
        }
    }
}
