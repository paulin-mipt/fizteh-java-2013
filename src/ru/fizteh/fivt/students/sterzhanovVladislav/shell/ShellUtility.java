package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.HashMap;
import java.nio.file.attribute.BasicFileAttributes;

public class ShellUtility {
    public static InputStream createStream(String[] args) {
        StringBuilder argline = new StringBuilder();
        for (String arg : args) {
            argline.append(arg).append(" ");
        }
        String cmdLine = argline.toString();
        return new ByteArrayInputStream(cmdLine.getBytes(Charset.defaultCharset()));
    }
    
    public static void execShell(String[] args, HashMap<String, Command> cmdMap) {
        try {
            Shell cmdShell = new Shell(cmdMap);
            if (args.length > 0) {
                InputStream cmdStream = createStream(args);
                cmdShell.execCommandStream(cmdStream, false);
            } else {
                cmdShell.execCommandStream(System.in, true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
    
    public static void removeDir(Path path) throws IOException {
        if (!path.toFile().exists() || !path.toFile().isDirectory()) {
            throw new IllegalStateException(path.toFile().getName() + " not exists");
        }
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw e;
                        }
                    }
                });
    }
}
