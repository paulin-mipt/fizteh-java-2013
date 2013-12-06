package ru.fizteh.fivt.students.nlevashov.shell;

import ru.fizteh.fivt.students.nlevashov.mode.Mode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.EnumSet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.DirectoryStream;

public class Shell {
    public static List<String> parse(String str, String separators) {
        String[] tokens = str.split(separators);
        List<String> tokensWithoutEmptyStrings = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                tokensWithoutEmptyStrings.add(tokens[i]);
            }
        }
        return tokensWithoutEmptyStrings;
    }

    static String currentDirectory = System.getProperty("user.dir");

    public static File makePath(String newPath) {
        File path = new File(currentDirectory);
        return path.toPath().resolve(newPath).normalize().toFile();
    }

    public static void cd(String path) throws IOException {
        File dir = makePath(path);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                currentDirectory = dir.toString();
            } else {
                throw new IOException("cd: Path \"" + path + "\" isn't a directory");
            }
        } else {
            throw new IOException("cd: Path \"" + path + "\" doesn't exist");
        }
    }

    public static void mkdir(String name) throws IOException {
        File dir = makePath(name);
        if (!dir.getCanonicalFile().mkdir()) {
            throw new IOException("mkdir: Directory \"" + name + "\" wasn't created");
        }
    }

    public static void pwd() {
        System.out.println(currentDirectory);
    }

    public static void rm(String path) throws IOException {
        File obj = makePath(path);
        if (!obj.exists()) {
            throw new IOException("rm: Object \"" + path + "\" doesn't exist");
        }
        Files.walkFileTree(obj.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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

    public static void cp(String from, String to) throws IOException {
        File path = makePath(from);
        if (!path.exists()) {
            throw new IOException("cp: Object \"" + from + "\" doesn't exist");
        }
        File destination = makePath(to);
        if (destination.exists()) {
            if (destination.isDirectory()) {
                final Path source = path.toPath();
                final Path target = destination.toPath().resolve(path.getName());
                if (Pattern.compile("^[\\.\\/]+").matcher(target.relativize(source).toString()).matches()) {
                    throw new IOException("cp: Can not copy folder to itself");
                }
                try {
                    Files.copy(source, target);
                } catch (FileAlreadyExistsException e) {
                    throw new IOException("cp: Object \"" + target.toString() + "\" already exist");
                }
                if (path.isDirectory()) {
                    Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                            Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                                throws IOException {
                            Path targetdir = target.resolve(source.relativize(dir));
                            try {
                                Files.copy(dir, targetdir);
                            } catch (FileAlreadyExistsException e) {
                                if (!Files.isDirectory(targetdir)) {
                                    throw new IOException("cp: Object \"" + targetdir.toString() + "\" already exist");
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            try {
                                Files.copy(file, target.resolve(source.relativize(file)));
                            } catch (FileAlreadyExistsException e) {
                                throw new IOException("cp: File \"" + file.toString() + "\" already exist");
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            } else {
                throw new IOException("cp: Path \"" + to + "\" isn't a directory");
            }
        } else {
            if (path.isDirectory()) {
                throw new IOException("cp: Path \"" + to + "\" doesn't exist");
            } else if (destination.getParentFile().exists()) {
                Files.copy(path.toPath(), destination.toPath());
            } else {
                throw new IOException("cp: Directory \"" + destination.getParentFile().toString() + "\" doesn't exist");
            }
        }
    }

    public static void mv(String from, String to) throws IOException {
        File path = makePath(from);
        if (!path.exists()) {
            throw new IOException("mv: Object \"" + from + "\" doesn't exist");
        }
        File destination = makePath(to);
        if (Pattern.compile("^[\\.\\/]+").
                matcher(destination.toPath().relativize(path.toPath()).toString()).matches()) {
            throw new IOException("mv: Can not move folder to itself");
        }
        if (path.isDirectory()) {
            if (destination.exists()) {
                if (destination.isDirectory()) {
                    try {
                        path.renameTo(destination.toPath().resolve(path.getName()).toFile());
                    } catch (SecurityException e) {
                        throw new IOException("mv: Object \"" + from + "\" wasn't moved (access denied)");
                    }
                } else {
                    throw new IOException("mv: Path \"" + to + "\" isn't a directory");
                }
            } else {
                throw new IOException("mv: Path \"" + to + "\" doesn't exist");
            }
        } else {
            if (destination.exists()) {
                if (destination.isDirectory()) {
                    try {
                        path.renameTo(destination.toPath().resolve(path.getName()).toFile());
                    } catch (SecurityException e) {
                        throw new IOException("mv: Object \"" + from + "\" wasn't moved (access denied)");
                    }
                } else {
                    throw new IOException("mv: Object \"" + to + "\" already exists");
                }
            } else if (destination.getParentFile().exists()) {
                try {
                    path.renameTo(destination);
                } catch (SecurityException e) {
                    throw new IOException("mv: Object \"" + from + "\" wasn't moved (access denied)");
                }
            } else {
                throw new IOException("mv: Directory \"" + destination.getParent() + "\" doesn't exist");
            }
        }
    }

    public static void dir() throws IOException {
        File state = new File(currentDirectory);
        DirectoryStream<Path> stream = Files.newDirectoryStream(state.toPath());
        for (Path f : stream) {
            System.out.println(f.getFileName().toString());
        }
    }

    public static void main(String[] args) {
        try {
            Mode.start(args, new Mode.Executor() {
                public boolean execute(String cmd) throws IOException {
                    List<String> tokens = parse(cmd, " ");
                    if (tokens.size() != 0) {
                        switch (tokens.get(0)) {
                            case "cd":
                                if (tokens.size() != 2) {
                                    throw new IOException("cd: wrong arguments number");
                                }
                                cd(tokens.get(1));
                                break;
                            case "mkdir":
                                if (tokens.size() != 2) {
                                    throw new IOException("mkdir: wrong arguments number");
                                }
                                mkdir(tokens.get(1));
                                break;
                            case "pwd":
                                if (tokens.size() != 1) {
                                    throw new IOException("pwd: wrong arguments number");
                                }
                                pwd();
                                break;
                            case "rm":
                                if (tokens.size() != 2) {
                                    throw new IOException("rm: wrong arguments number");
                                }
                                rm(tokens.get(1));
                                break;
                            case "cp":
                                if (tokens.size() != 3) {
                                    throw new IOException("cp: wrong arguments number");
                                }
                                cp(tokens.get(1), tokens.get(2));
                                break;
                            case "mv":
                                if (tokens.size() != 3) {
                                    throw new IOException("mv: wrong arguments number");
                                }
                                mv(tokens.get(1), tokens.get(2));
                                break;
                            case "dir":
                                if (tokens.size() != 1) {
                                    throw new IOException("dir: wrong arguments number");
                                }
                                dir();
                                break;
                            case "exit": {
                                return false;
                            }
                            default:
                                throw new IOException("Wrong command: " + cmd);
                        }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
