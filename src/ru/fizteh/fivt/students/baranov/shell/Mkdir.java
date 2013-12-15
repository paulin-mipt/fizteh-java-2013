package ru.fizteh.fivt.students.baranov.shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Mkdir extends BasicCommand {
    public int doCommand(String[] args, ShellState currentPath) {
        if (args.length != 2) {
            System.err.println("mkdir needs 1 argument");
            return 1;
        }
        Path newPath = Paths.get(currentPath.getCurrentPath().toString() + "/" + args[1]);
        try {
            Path dir = Files.createDirectory(newPath);
        } catch (IOException exception) {
            System.err.println("mkdir: input/output error");
            return 1;
        }
        return 0;
    }
}
