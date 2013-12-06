package ru.fizteh.fivt.students.asaitgalin.shell.commands;

import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;
import ru.fizteh.fivt.students.asaitgalin.shell.FilesystemController;

import java.io.File;
import java.io.IOException;

public class CpCommand extends DefaultCommand {
    private FilesystemController controller;

    public CpCommand(FilesystemController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "cp";
    }

    @Override
    public void execute(String[] args) throws IOException {
        File source = controller.getFileFromName(args[1]);
        File target = controller.getFileFromName(args[2]);
        if (!source.exists()) {
            throw new IOException("cp: \"" + args[1] + "\": No such file or directory");
        }
        if (source.equals(target)) {
            throw new IOException("cp: \"" + args[1] + "\" and \"" + args[2] + "\" are the same file");
        }
        if (source.isFile()) {
            if (!target.exists()) {
                controller.copyFile(source, target);
            } else {
                if (target.isDirectory()) {
                    copyRecursive(source, target);
                } else {
                    throw new IOException("cp: can not copy file to existing file");
                }
            }
        } else if (source.isDirectory()) {
            if (target.isFile() || !target.exists()) {
                throw new IOException("cp: omitting directory \"" + args[1] + "\"");
            } else {
                copyRecursive(source, target);
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 2;
    }

    public void copyRecursive(File src, File dest) throws IOException {
        File destFile = new File(dest, src.getName());
        if (destFile.exists()) {
            throw new IOException("cp: \"" + src.getName() + "\": already exists");
        }
        if (src.isDirectory()) {
            destFile.mkdir();
            for (File f : src.listFiles()) {
                copyRecursive(f, destFile);
            }
        }
        controller.copyFile(src, destFile);
    }

}
