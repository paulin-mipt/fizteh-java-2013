package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

import java.io.IOException;

public class PwdCommand extends DefaultCommand {
    private FilesystemController controller;

    public PwdCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "pwd";
    }

    @Override
    public void execute(String[] args) throws IOException {
        System.out.println(controller.getCurrentDir().getAbsolutePath());
    }

    @Override
    public int getArgsCount() {
        return 0;
    }

}
