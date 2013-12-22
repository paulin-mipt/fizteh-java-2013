package ru.fizteh.fivt.students.baranov.shell;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class FileTreeCopy implements FileVisitor<Path> {
    private Path source;
    private Path target;
    public int error;

    FileTreeCopy(Path s, Path t) {
        this.source = s;
        this.target = t;
        this.error = 0;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        Path newDir = target.resolve(source.getParent().relativize(dir));
        try {
            Files.copy(dir, newDir);
        } catch (FileAlreadyExistsException x) {
            // nothing
        } catch (IOException x) {
            System.err.format("Unable to create: %s: %s%n, skipping the subtree.", newDir, x);
            error = 1;
            return SKIP_SUBTREE;
        }

        return CONTINUE;
    }

    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        try {
            Files.copy(file, target.resolve(source.getParent().relativize(file)));
        } catch (IOException e) {
            System.err.println(e);
            error = 1;
        }

        return CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        if (exc == null) {
            Path newDir = target.resolve(source.getParent().relativize(dir));
            try {
                FileTime time = Files.getLastModifiedTime(dir);
                Files.setLastModifiedTime(newDir, time);
            } catch (IOException x) {
                System.err.println(x.getMessage());
                error = 1;
            }
        }

        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        if (exc instanceof FileSystemLoopException) {
            throw new IllegalArgumentException("Cycle detected while copying file: " + file.toString());
        }

        System.err.println(exc);
        error = 1;
        return CONTINUE;
    }
}

