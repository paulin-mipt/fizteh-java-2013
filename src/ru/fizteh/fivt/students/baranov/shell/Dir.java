package ru.fizteh.fivt.students.baranov.shell;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Dir extends BasicCommand {
    public int doCommand(String[] args, ShellState currentPath) {
        if (args.length != 1) {
            System.err.println("dir doesn't need arguments");
            return 1;
        }

        Path dir = currentPath.getCurrentPath();
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            try {
                for (Path file : stream) {
                    System.out.println(file.getFileName());
                }
            } catch (DirectoryIteratorException x) {
                System.err.println(x);
                return 1;
            }
        } catch (IOException e) {
            System.err.println(e);
            return 1;
        }
        return 0;
    }
}
