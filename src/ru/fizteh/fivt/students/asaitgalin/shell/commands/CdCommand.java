package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

import java.io.IOException;

public class CdCommand extends DefaultCommand {
    private FilesystemController controller;

    public CdCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "cd";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (!controller.changeDir(args[1])) {
            throw new IOException("cd: \"" + args[1] + "\": Not a directory or does not exist");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

}
