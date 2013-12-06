package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

import java.io.File;
import java.io.IOException;

public class MkdirCommand extends DefaultCommand {
    private FilesystemController controller;

    public MkdirCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "mkdir";
    }

    @Override
    public void execute(String[] args) throws IOException {
        File f = controller.getFileFromName(args[1]);
        if (!f.mkdirs()) {
            throw new IOException("mkdir: \"" + args[1] + "\": Failed to create directory");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}

