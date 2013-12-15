package ru.fizteh.fivt.students.baranov.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.*;

public class Move extends BasicCommand {
    public int doCommand(String[] args, ShellState currentPath) {
        if (args.length != 3) {
            System.err.println("mv needs 2 arguments");
            return 1;
        }

        Path tempPath = Paths.get(args[1]).normalize();
        Path sourcePath = currentPath.getCurrentPath().resolve(tempPath);
        sourcePath = sourcePath.toAbsolutePath();

        tempPath = Paths.get(args[2]).normalize();
        Path targetPath = currentPath.getCurrentPath().resolve(tempPath);
        targetPath = targetPath.toAbsolutePath();

        if (Files.isDirectory(sourcePath) && Files.isRegularFile(targetPath)) {
            System.err.println("can't move directory to file");
            return 1;
        }

        try {
            Files.move(sourcePath, targetPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(e);
            return 1;
        }

        return 0;
    }
}
