package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;

public class PwdCommand extends Command {
    public PwdCommand() {
        name = "pwd";
        argumentsAmount = 0;
    }

    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        File path = Shell.getPath();
        System.out.println(path.getCanonicalPath());
    }
}
