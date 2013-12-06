package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MvCommand extends DefaultCommand {
    private FilesystemController controller;

    public MvCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "mv";
    }

    @Override
    public void execute(String[] args) throws IOException {
        File source = controller.getFileFromName(args[1]);
        File target = controller.getFileFromName(args[2]);
        if (!source.exists()) {
            throw new IOException("mv: \"" + args[1] + "\": No such file or directory");
        }
        if (source.equals(target)) {
            throw new IOException("mv: \"" + args[1] + "\" and \"" + args[2] + "\" are the same file");
        }
        if (!target.exists()) {
            Files.move(source.toPath(), target.toPath());
        } else {
            if (target.isDirectory()) {
                File moveTarget = new File(target, source.getName());
                Files.move(source.toPath(), moveTarget.toPath());
            } else {
                throw new IOException("mv: can not move\"" + source.getName() + "\" to \"" + target.getName() + "\"");
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 2;
    }

}
