package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class RmCommand implements Command<Shell> {
    private static void rm(final Path removing) throws IOException {
        File remove = new File(removing.toString());
        if (!remove.exists()) {
            throw new IOException(removing + " there is not such file or directory");
        }
        if (remove.isFile()) {
            if (!remove.delete()) {
                throw new IOException(removing + " can't remove this file");
            }
        }
        if (remove.isDirectory()) {
            String[] fileList = remove.list();
            for (String files : fileList) {
                rm(removing.resolve(files));
            }
            if (!remove.delete()) {
                throw new IOException(removing + " can't remove this directory");
            }
        }
    }

    @Override
    public final boolean exec(Shell state, final String[] command) {
        if (command.length != 2) {
            System.err.println("rm: Usage - rm <file|dir>");
            return false;
        }
        try {
            File removing = new File(Shell.getUserDir().toPath().resolve(command[1]).toString());
            rm(removing.toPath());
        } catch (IOException e) {
            System.err.println("rm: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("rm: can't remove " + command[1]);
            return false;
        }
        return true;
    }

    @Override
    public final String commandName() {
        return "rm";
    }
}
