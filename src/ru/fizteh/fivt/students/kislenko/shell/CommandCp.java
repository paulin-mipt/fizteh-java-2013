package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandCp implements Command<ShellState> {
    public String getName() {
        return "cp";
    }

    public int getArgCount() {
        return 2;
    }

    private static void copyFile(File source, File dest) throws IOException {
        Path target = dest.toPath().resolve(source.getName());
        if (source.isFile()) {
            Files.copy(source.toPath(), target);
        } else {
            File[] sourceEntries = source.listFiles();
            target.toFile().mkdir();
            for (File sourceEntry : sourceEntries != null ? sourceEntries : new File[0]) {
                copyFile(sourceEntry, target.toFile());
            }
        }
    }

    public void run(ShellState state, String[] args) throws IOException {
        if (args.length != 2) {
            throw new IOException("cp: Command \"cp\" takes one argument.");
        }
        String source = args[0];
        String dest = args[1];
        Path absolutePath = state.getState();
        Path sourcePath = absolutePath.resolve(source).normalize();
        Path destPath = absolutePath.resolve(dest).normalize();
        if (!destPath.getParent().toFile().exists()) {
            throw new IOException("cp: Cannot copy something on directory that doesn't exist");
        }
        if (destPath.toString().equals(sourcePath.toString())) {
            throw new IOException("cp: Cannot copy file on itself.");
        }
        if (sourcePath.toFile().isFile() && !destPath.toFile().isDirectory()) {
            if (destPath.toFile().isFile()) {
                throw new IOException("cp: Destination file is already exist.");
            } else {
                Files.copy(sourcePath, destPath);
            }
        } else if (sourcePath.toFile().isFile() && destPath.toFile().isDirectory()) {
            copyFile(sourcePath.toFile(), destPath.toFile());
        } else if (sourcePath.toFile().isDirectory() && destPath.toFile().isFile()) {
            throw new IOException("cp: Cannot copy directory to file");
        } else if (sourcePath.toFile().isDirectory() && destPath.toFile().isDirectory()) {
            if (destPath.startsWith(sourcePath)) {
                throw new IOException("cp: Cannot copy directory on itself.");
            }
            copyFile(sourcePath.toFile(), destPath.toFile());
        } else if (sourcePath.toFile().isDirectory()) {
            File[] sourceEntries = sourcePath.toFile().listFiles();
            destPath.toFile().mkdir();
            for (File sourceEntry : sourceEntries != null ? sourceEntries : new File[0]) {
                copyFile(sourceEntry, destPath.toFile());
            }
        } else {
            throw new IOException("cp: Incorrect file names.");
        }
    }
}
