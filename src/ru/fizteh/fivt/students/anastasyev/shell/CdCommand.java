package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.io.IOException;

public class CdCommand implements Command<Shell> {
    private static void cd(final String dir) throws IOException {
        File newUserDir = new File(dir);
        if (!newUserDir.isAbsolute()) {
            newUserDir = new File(Shell.getUserDir().getAbsoluteFile().toPath().resolve(dir).toString());
        }
        if (!newUserDir.isDirectory()) {
            throw new IOException(dir + " directory doesn't exist");
        }
        Shell.setUserDir(newUserDir);
    }

    @Override
    public final boolean exec(Shell state, final String[] command) {
        if (command.length != 2) {
            System.err.println("cd: Usage - cd <absolute path|relative path>");
            return false;
        }
        try {
            cd(command[1]);
        } catch (IOException e) {
            System.err.println("cd: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("cd: can't change directory");
            return false;
        }
        return true;
    }

    @Override
    public final String commandName() {
        return "cd";
    }
}
