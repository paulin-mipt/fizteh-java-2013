package ru.fizteh.fivt.students.mikhaylova_daria.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MyFileSystem {
    private static File currentDirectory = new File(".");

    public static void changeDir(String[] argFirst) throws Exception {
        String[] arg = argFirst[1].split("\\s+");
        if (arg.length != 1) {
            throw new Exception("Wrong number of arguments");
        }
        String pathString = arg[0];
        File newDir = new File(pathString);
        if (!newDir.isAbsolute()) {
            newDir = currentDirectory.toPath().resolve(newDir.toPath()).toFile();
        }
        if (newDir.isDirectory()) {
            currentDirectory = newDir;
        } else {
            throw new Exception(pathString + "': It's not a directory");
        }
    }

    public static void makeDir(String[] commandFirst) throws Exception {
        String[] command = commandFirst[1].split("\\s+");
        if (command.length != 1) {
            throw new Exception("Wrong number of arguments");
        }
        File newDir = new File(command[0]);
        if (!newDir.isAbsolute()) {
            newDir = currentDirectory.toPath().resolve(newDir.toPath()).normalize().toFile();
        }
        if (!newDir.mkdirs()) {
            throw new Exception("A directory with the same name already exists: " + newDir.getAbsolutePath());
        }
    }

    public static void printWorkingDir(String[] command) throws Exception {
        if (command.length != 1) {
            throw new Exception("Wrong number of arguments");
        }
        System.out.println(currentDirectory.getAbsoluteFile().toPath().normalize().toString());
    }

    public static void dir(String[] commandFirst) throws Exception {
        if (commandFirst.length != 1) {
            throw new Exception("Wrong number of arguments");
        }
        String[] s = currentDirectory.list();
        int i;
        for (i = 0; i < s.length; i++) {
            System.out.println(s[i]);
        }
    }

    public static void remove(String[] commandFirst) throws Exception {
        String[] command = commandFirst[1].split("\\s+");
        if (command.length != 1) {
            throw new Exception("Wrong number of arguments");
        }
        if (command[0].equals(".")) {
            command[0] = currentDirectory.toString();
        } else {
            if (command[0].equals("..")) {
                command[0] = currentDirectory.getParent();
            }
        }
        if (command[0].charAt(0) == '.') {
            command[0] = command[0].substring(2);
        }
        removing(command[0]);
    }

    public static void removing(String fileName)throws IOException {
            File name = new File(fileName);
            if (!name.toPath().isAbsolute()) {
                name = currentDirectory.toPath().resolve(name.toPath()).normalize().toFile();
            }
            if (name.isFile()) {
                Files.delete(name.toPath());
            } else {
                Path start = name.toPath();
                Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

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
                if (!currentDirectory.toPath().normalize().toAbsolutePath().toFile().exists()) {
                    currentDirectory = currentDirectory.toPath().getRoot().toFile();
                }
            }
    }

    public static void copy(String[] argFirst) throws Exception {
        String[] arg = argFirst[1].split("\\s+");
        if (arg.length != 2) {
            throw new Exception("Wrong number of arguments");
        }
        String sourceStr = arg[0];
        String destination = arg[1];
        if (sourceStr.equals(".")) {
            sourceStr = currentDirectory.toPath().toAbsolutePath().normalize().toString();
        } else {
            if (sourceStr.equals("..")) {
                sourceStr = currentDirectory.toPath().normalize().toAbsolutePath().
                        getParent().toString();
            }
        }
        if (destination.equals(".")) {
            destination = currentDirectory.toPath().toAbsolutePath().normalize().toString();
        } else {
            if (destination.equals("..")) {
                destination = currentDirectory.toPath().normalize().toAbsolutePath().getParent().toString();
            }
        }
        File arg1 = new File(sourceStr);
        File arg2 = new File(destination);
        if (!arg1.isAbsolute()) {
            arg1 = currentDirectory.toPath().normalize().resolve(arg1.toPath()).normalize().toFile();
        }
        if (!arg2.isAbsolute()) {
            arg2 = currentDirectory.toPath().normalize().resolve(arg2.toPath()).normalize().toFile();
        }
        if (!arg1.toPath().toAbsolutePath().toFile().exists()) {
            throw new Exception("File source not found");
        }
        if (!arg2.toPath().toAbsolutePath().toFile().exists()) {
            Files.createFile(arg2.toPath().toAbsolutePath());
        }
        final Path source = arg1.toPath().toAbsolutePath();
        Path targetDir = arg2.toPath().toAbsolutePath();
        final Path target = arg2.toPath().toAbsolutePath();
        if (targetDir.startsWith(source)) {
            if (targetDir.equals(source)) {
                throw new Exception("Copying is not possible: this arguments are the same");
            }
            throw new Exception("Copying to a subfolder is impossible");
        }
        if (source.toFile().isFile() && target.toFile().isFile()) {
            Files.copy(source, target, REPLACE_EXISTING);
            return;
        }
        if (source.toFile().isFile() && target.toFile().isDirectory()) {
            Files.copy(source, target.resolve(source.getFileName()), REPLACE_EXISTING);
            return;
        }
        if (targetDir.toFile().isFile() && source.toFile().isDirectory()) {
            throw new Exception("Copying a directory to file");
        }
        if (!target.resolve(source.getFileName()).toFile().mkdirs()) {
            throw new Exception("A directory with the same name already exists in the target path\n");
        }
        final Path tar = target.resolve(source.getFileName());
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = tar.resolve(source.relativize(dir)).normalize();
                        try {
                            Files.copy(dir, targetDir);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetDir)) {
                                throw e;
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.copy(file, tar.resolve(source.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    public static void move(String[] argFirst) throws Exception {
        String[] arg = argFirst[1].split("\\s+");
        if (!(arg.length == 2)) {
            throw new Exception("Wrong number of arguments");
        }
        String argF = arg[0] + " " + arg[1];
        String[] command = new String[] {"cp", argF};
        copy(command);
        String[] command1 = new String[] {"rm", arg[0]};
        try {
            remove(command1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exit(String[] arg) {
        System.exit(0);
    }
}
