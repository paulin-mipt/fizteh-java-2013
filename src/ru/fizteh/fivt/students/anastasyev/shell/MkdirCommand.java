package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.io.IOException;

public class MkdirCommand implements Command<Shell> {
    private static boolean mkdir(final String dir) throws IOException {
        File newDir = new File(Shell.getUserDir().toPath().resolve(dir).toString());
        if (newDir.exists()) {
            System.err.println("mkdir: " + dir + " already exists");
            return false;
        }
        if (!newDir.mkdir()) {
            throw new IOException();
        }
        return true;
    }

    @Override
    public final boolean exec(Shell state, final String[] command) {
        if (command.length != 2) {
            System.err.println("mkdir: Usage - mkdir <dirname>");
            return false;
        }
        boolean result = false;
        try {
            result = mkdir(command[1]);
        } catch (Exception e) {
            System.err.println("mkdir: can't create " + command[1]);
            return false;
        }
        return result;
    }

    @Override
    public final String commandName() {
        return "mkdir";
    }
}
