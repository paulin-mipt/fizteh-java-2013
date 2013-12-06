package ru.fizteh.fivt.students.ichalovaDiana.shell;

import java.util.EnumSet;
import java.util.Hashtable;
import java.io.IOException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.StandardCopyOption;

public class Shell {

    static Hashtable<String, Command> commands = new Hashtable<String, Command>();
    static Interpreter interpreter;
    static Path workingDirectory;

    static {
        workingDirectory = Paths.get(System.getProperty("user.dir"));

        commands.put("cd", new Cd());
        commands.put("mkdir", new Mkdir());
        commands.put("pwd", new Pwd());
        commands.put("rm", new Rm());
        commands.put("cp", new Cp());
        commands.put("mv", new Mv());
        commands.put("dir", new Dir());
        commands.put("exit", new Exit());

        interpreter = new Interpreter(commands);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            interpreter.runInteractiveMode();
        } else {
            interpreter.runBatchMode(args);
        }
    }

    static Path getWorkingDirectory() {
        return workingDirectory;
    }

    static void setWorkingDirectory(Path newWorkingDirectory) {
        workingDirectory = newWorkingDirectory.normalize();
    }

    static Path getAbsolutePath(Path path) {
        return getWorkingDirectory().resolve(path).normalize();
    }

    static boolean isSubdirectory(Path candidate, Path directory) {
        Path checkedDirectory = candidate.normalize().getParent();
        directory = directory.normalize();
        while (!checkedDirectory.equals(checkedDirectory.getRoot())) {
            if (checkedDirectory.equals(directory)) {
                return true;
            }
            checkedDirectory = checkedDirectory.getParent();
        }
        return false;
    }
}

class Cd extends Command {
    static final int ARG_NUM = 2;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            Path changePath = Paths.get(arguments[1]);
            changePath = Shell.getAbsolutePath(changePath);
            if (changePath.toFile().isDirectory()) {
                Shell.setWorkingDirectory(changePath);
            } else {
                throw new IllegalArgumentException("'" + arguments[1] + "': "
                        + "No such file or directory");
            }
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Mkdir extends Command {
    static final int ARG_NUM = 2;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            Path newPath = Paths.get(arguments[1]);
            newPath = Shell.getAbsolutePath(newPath);
            if (!newPath.toFile().mkdir()) {
                throw new Exception("Couldn't create a directory");
            }
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Pwd extends Command {
    static final int ARG_NUM = 1;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            System.out.println(Shell.getWorkingDirectory());
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Rm extends Command {
    static final int ARG_NUM = 2;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            Path path = Paths.get(arguments[1]);
            path = Shell.getAbsolutePath(path);
            if (path.toFile().isFile()) {
                Files.delete(path);
            } else {
                if (Shell.isSubdirectory(Shell.getWorkingDirectory(), path)
                        || Shell.getWorkingDirectory().equals(path)) {
                    throw new IllegalArgumentException(
                            "Working directory is a subdirectory of " + path);
                }
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e)
                            throws IOException {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw e;
                        }
                    }
                });
            }
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Cp extends Command {
    static final int ARG_NUM = 3;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            Path source = Paths.get(arguments[1]);
            Path destination = Paths.get(arguments[2]);
            source = Shell.getAbsolutePath(source);
            destination = Shell.getAbsolutePath(destination);

            if (source.toFile().isFile() && destination.toFile().isDirectory()) {
                Files.copy(source, destination.resolve(source.getFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
            } else if (source.toFile().isFile()) {
                if (source.equals(destination)) {
                    throw new Exception(source + " and " + destination
                            + " are the same files");
                }
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } else if (source.toFile().isDirectory()
                    && destination.toFile().isDirectory()) { // recursive
                if (Shell.isSubdirectory(destination, source)
                        || destination.equals(source)) {
                    throw new FileSystemLoopException(destination
                            + " is a subdirectory of " + source);
                }
                final Path src = source;
                final Path dst = destination;
                Files.walkFileTree(src, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs) throws IOException {
                                Path targetdir = dst.resolve(src.getParent().relativize(
                                        dir));
                                Files.copy(dir, targetdir,
                                        StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file,
                                    BasicFileAttributes attrs) throws IOException {
                                Files.copy(file,
                                        dst.resolve(src.getParent().relativize(file)),
                                        StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }

                        });
            } else {
                throw new Exception("cannot copy directory to file");
            }
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Mv extends Command {
    static final int ARG_NUM = 3;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            Path source = Paths.get(arguments[1]);
            Path destination = Paths.get(arguments[2]);
            source = Shell.getAbsolutePath(source);
            destination = Shell.getAbsolutePath(destination);

            if (source.toFile().isFile() && destination.toFile().isDirectory()) {
                Files.move(source, destination.resolve(source.getFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
            } else if (source.toFile().isFile()) {
                Files.move(source, destination);
            } else if (source.toFile().isDirectory()
                    && destination.toFile().isDirectory()) {
                if (Shell.isSubdirectory(destination, source)
                        || destination.equals(source)) {
                    throw new FileSystemLoopException(destination
                            + " is a subdirectory of " + source);
                }
                final Path src = source;
                final Path dst = destination;
                Files.walkFileTree(src, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs) throws IOException {
                                Path targetdir = dst.resolve(src.getParent().relativize(
                                        dir));
                                Files.move(dir, targetdir,
                                        StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file,
                                    BasicFileAttributes attrs) throws IOException {
                                Files.move(file, dst.resolve(src.relativize(file)),
                                        StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }
                        });
            } else {
                throw new Exception("cannot move directory to file");
            }
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Dir extends Command {
    static final int ARG_NUM = 1;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            Path currentPath = Shell.getWorkingDirectory();
            for (String file : currentPath.toFile().list()) {
                System.out.println(file);
            }
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Exit extends Command {
    static final int ARG_NUM = 1;
    public boolean rawArgumentsNeeded = false;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            System.exit(0);
        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}
