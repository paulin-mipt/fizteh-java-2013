package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CpCommand extends Command {

    public CpCommand() {
        name = "cp";
        argumentsAmount = 2;
    }

    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        realRun(args[0], args[1]);
    }

    private void realRun(String sourceName, String destinationName) throws Exception {
        File source = new File(Shell.getFullPath(sourceName));
        File destination = new File(Shell.getFullPath(destinationName));
        if (source.isDirectory()) {
            Path sourcePath = Paths.get(source.getCanonicalPath());
            if (destination.isDirectory()) {
                destinationName = destinationName + File.separator + source.getName();
                destination = new File(Shell.getFullPath(destinationName));
            }
            Path destinationPath = Paths.get(destination.getCanonicalPath());
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            File[] children = source.listFiles();
            if (children != null) {
                for (File child : children) {
                    String currFileName = child.getName();
                    realRun(child.getCanonicalPath(), destinationName + File.separator + currFileName);
                }
            }
        } else {
            if (destination.getCanonicalPath().equals(source.getCanonicalPath())) {
                throw new IllegalArgumentException("Files are identical");
            }
            Path sourcePath = Paths.get(source.getCanonicalPath());
            Path destinationPath;
            if (destination.isDirectory()) {
                destinationPath = Paths.get(destination.getCanonicalPath() + File.separator + source.getName());
            } else {
                destinationPath = Paths.get(destination.getCanonicalPath());
            }
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
