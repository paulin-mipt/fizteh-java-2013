package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import java.io.File;
import java.io.IOException;
import java.lang.System;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MoveCommand extends Command {
    public boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
            return false;
        }
        Path sourcePath = Paths.get(args[0]);
        if (curState.workingDirectory != null && !sourcePath.isAbsolute()) {
            sourcePath = Paths.get(curState.workingDirectory).resolve(sourcePath);
        }
        File source = sourcePath.normalize().toFile();
        Path destinationPath = Paths.get(args[1]);
        if (curState.workingDirectory != null && !destinationPath.isAbsolute()) {
            destinationPath = Paths.get(curState.workingDirectory).resolve(destinationPath);
        }
        File destination = destinationPath.normalize().toFile();
        if ((!source.isDirectory()) || (!destination.isDirectory())) {
            System.err.println("It is not a directory");
            return false;
        }
        if ((!source.exists()) || (!destination.exists())) {
            System.err.println("Error with moving files");
            return false;
        }
        Files.move(sourcePath, destinationPath, REPLACE_EXISTING);
        return true;
    }

    public String getCmd() {
        return "mv";
    }
}