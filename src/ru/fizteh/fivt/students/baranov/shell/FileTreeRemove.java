package ru.fizteh.fivt.students.baranov.shell;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class FileTreeRemove implements FileVisitor<Path> {
    private Path source;
    public int error;

    FileTreeRemove(Path s) {
        this.source = s;
        this.error = 0;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        dir = source.resolve(source.getParent().relativize(dir));
        try {
            Files.delete(dir);
        } catch (NoSuchFileException x) {
            // nothing
        } catch (IOException x) {
            System.err.format("Unable to delete: %s:%n, skipping the subtree.", x);
            error = 1;
            return SKIP_SUBTREE;
        }

        return CONTINUE;
    }

    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("input/output error while deleting file: " + file.toString());
        }

        return CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        if (exc instanceof FileSystemLoopException) {
            throw new IllegalArgumentException("Cycle detected while deleting file: " + file.toString());
        }

        System.err.println(exc);
        error = 1;
        return CONTINUE;
    }
}


