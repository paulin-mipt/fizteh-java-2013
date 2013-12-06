package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

import java.io.File;
import java.io.IOException;

public class RmCommand extends DefaultCommand {
    private FilesystemController controller;

    public RmCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "rm";
    }

    @Override
    public void execute(String[] args) throws IOException {
        deleteRecursively(controller.getFileFromName(args[1]));
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    private void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursively(f);
            }
        }
        if (!file.delete()) {
            throw new IOException("rm: cannot remove \"" + file.getName() + "\": No such file or directory");
        }
    }

}
