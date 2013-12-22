package ru.fizteh.fivt.students.baranov.shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Remove extends BasicCommand {
    public int doCommand(String[] args, ShellState currentPath) {
        if (args.length != 2) {
            System.err.println("rm needs 1 argument");
            return 1;
        }

        Path tempPath = Paths.get(args[1]).normalize();
        Path newPath = currentPath.getCurrentPath().resolve(tempPath);
        newPath = newPath.toAbsolutePath();

        if (!Files.exists(newPath)) {
            System.err.println("file doesn't already exist: " + newPath.toString());
            return 1;
        }
        if (!Files.isDirectory(newPath)) {
            try {
                Files.delete(newPath);
            } catch (IOException e) {
                System.err.println("can't delete file: " + newPath.toString());
                return 1;
            }
            return 0;
        } else {
            FileTreeRemove fileTree = new FileTreeRemove(newPath);
            try {
                Files.walkFileTree(newPath, fileTree);
            } catch (IOException e) {
                System.err.println("error in removing directory" + newPath.toString());
                return 1;
            }

            if (fileTree.error == 1) {
                return 1;
            }

            try {
                Files.delete(newPath);
            } catch (IOException e) {
                System.err.println("can't delete directory: " + newPath.toString());
                return 1;
            }
            return 0;
        }
    }
}
