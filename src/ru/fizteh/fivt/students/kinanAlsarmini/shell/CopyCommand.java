package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;

class CopyCommand extends ExternalCommand {
    public CopyCommand() {
        super("cp", 2);
    }

    public void execute(String[] args, Shell shell) {
        File source = Utilities.getAbsoluteFile(args[0], shell.getCurrentPath());
        File destination = Utilities.getAbsoluteFile(args[1], shell.getCurrentPath());

        if (!source.exists()) {
            throw new IllegalArgumentException("cp: source doesn't exist.");
        }

        if (destination.isDirectory()) {
            try {
                if (source.isDirectory()) {
                    Utilities.TreeCopier tc = new Utilities.TreeCopier(source.toPath(),
                            destination.toPath());
                    Files.walkFileTree(source.toPath(), tc);
                } else {
                    Files.copy(source.toPath(), destination.toPath().resolve(source.getName()));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("cp: unable to read source.");
            }
        } else {
            if (destination.exists()) {
                throw new IllegalArgumentException("cp: destination exists and is not a directory.");
            } else if (!destination.getParentFile().exists()) {
                throw new IllegalArgumentException("cp: destination is invalid.");
            } else {
                try {
                    Files.copy(source.toPath(), destination.toPath());
                } catch (IOException e) {
                    throw new IllegalArgumentException("cp: I/O error while copying.");
                }
            }
        }
    }
}
