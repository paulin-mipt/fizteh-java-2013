package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;

public class MkdirCommand extends Command {
    public MkdirCommand() {
        name = "mkdir";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("usage");
        }
        String directoryName = args[0];
        File newDirectory = new File(Shell.getFullPath(directoryName));
        if (!newDirectory.exists()) {
            if (!newDirectory.mkdir()) {
                throw new IllegalArgumentException(directoryName + ": It is impossible to create a directory");
            }
        } else {
            throw new IllegalArgumentException(directoryName + ": File/directory exists");
        }
    }




}
