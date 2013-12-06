package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;

public class RmCommand extends Command {
    public RmCommand() {
        name = "rm";
        argumentsAmount = 1;
    }

    public void run(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("invalid usage");
        }
        String name = args[0];
        realRun(name);

    }

    private void realRun(String name) throws Exception {
        File currFile = new File(Shell.getFullPath(name));
        if (!currFile.exists()) {
            throw new IllegalArgumentException(name + ": No such file or directory");
        }
        File[] children = currFile.listFiles();
        if (children != null) {
            if (currFile.isDirectory()) {
                for (File child : children) {
                    realRun(child.toString());
                }
            }
        }
        if (!currFile.delete()) {
            throw new IllegalArgumentException(name + ": Can't delete");
        }

    }
}
