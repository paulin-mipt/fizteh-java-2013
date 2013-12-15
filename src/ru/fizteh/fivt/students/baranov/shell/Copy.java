package ru.fizteh.fivt.students.baranov.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Copy extends BasicCommand {
    public int doCommand(String[] args, ShellState currentPath) {
        if (args.length != 3) {
            System.err.println("copy needs 2 arguments");
            return 1;
        }

        Path tempPath = Paths.get(args[1]).normalize();
        Path sourcePath = currentPath.getCurrentPath().resolve(tempPath);
        sourcePath = sourcePath.toAbsolutePath();

        tempPath = Paths.get(args[2]).normalize();
        Path targetPath = currentPath.getCurrentPath().resolve(tempPath);
        targetPath = targetPath.toAbsolutePath();

        if (!Files.exists(sourcePath)) {
            System.err.println("source doesn't exist: " + sourcePath.toString());
            return 1;
        }
        if (Files.exists(targetPath)) {
            boolean flag = false;
            try {
                flag = Files.isSameFile(sourcePath, targetPath);
            } catch (IOException e) {
                System.err.println(e);
                return 1;
            }
            if (flag) {
                System.err.println("same files");
                return 1;
            }
        }
        if (Files.isRegularFile(targetPath)) {
            if (Files.isRegularFile(sourcePath)) {
                try {
                    Files.copy(sourcePath, targetPath);
                } catch (IOException e) {
                    System.err.println(e);
                    return 1;
                }
                return 0;
            }
            if (Files.isDirectory(sourcePath)) {
                System.err.println("can't cope directory to file");
                return 1;
            }
        }
        if (Files.isRegularFile(sourcePath) && !Files.exists(targetPath)) {
            try {
                Files.copy(sourcePath, targetPath);
            } catch (IOException e) {
                System.err.println(e);
                return 1;
            }
            return 0;
        }

        FileTreeCopy fileTree = new FileTreeCopy(sourcePath, targetPath);
        try {
            Files.walkFileTree(sourcePath, fileTree);
        } catch (IOException e) {
            System.err.println(e);
            return 1;
        }
        if (fileTree.error == 1) {
            return 1;
        }
        return 0;
    }
}
