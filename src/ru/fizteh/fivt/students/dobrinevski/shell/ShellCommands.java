package ru.fizteh.fivt.students.dobrinevski.shell;

import java.io.File;
import java.nio.file.*;

public class ShellCommands {
    public static class Cd extends Command {
        @Override
        public void innerExecute(String[] args) throws Exception {
            File tmpFile = new File(args[1]);
            if (!tmpFile.isAbsolute()) {
                tmpFile = new File(parentShell.currentDir.getCanonicalPath() + File.separator + args[1]);
            }
            if (tmpFile.isDirectory()) {
                parentShell.currentDir = tmpFile;
            } else if (!tmpFile.exists()) {
                throw new Exception("\'" + args[1] + "\': No such file or directory");
            } else {
                throw new Exception("\'" + args[1] + "\': Not a directory");
            }
        }
        public Cd() {
            super(2);
        }
    }

    public static class Mkdir extends Command {
        @Override
        public void innerExecute(String[] args) throws Exception {
            File tmpFile = new File(args[1]);
            if (!tmpFile.isAbsolute()) {
                tmpFile = new File(parentShell.currentDir.getCanonicalPath() + File.separator + args[1]);
            }
            if (tmpFile.exists()) {
                throw new Exception("\'" + args[1] + "\': File or directory exist in time");
            }
            if (!tmpFile.mkdir()) {
                throw new Exception("\'" + args[1] + "\': Directory wasn't created");
            }
        }
        public Mkdir() {
            super(2);
        }
    }

    public static class Pwd extends Command {
        @Override
        public void innerExecute(String[] args) {
            returnValue = new String[1];
            returnValue[0] = parentShell.currentDir.toPath().normalize().toString();
        }
        public Pwd() {
            super(1);
        }
    }

    public static class Remove extends Command {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Path pathToRemove = parentShell.currentDir.toPath().resolve(args[1]).normalize();
            if (!Files.exists(pathToRemove)) {
                throw new Exception("Cannot be removed: File does not exist");
            }
            if (parentShell.currentDir.toPath().normalize().startsWith(pathToRemove)) {
                throw new Exception("\'" + args[1]
                        + "\': Cannot be removed: First of all, leave this directory");
            }
            File fileToRemove = new File(args[1]);
            if (!fileToRemove.isAbsolute()) {
                fileToRemove = new File(parentShell.currentDir.getCanonicalPath() + File.separator + args[1]);
            }
            File[] filesToRemove = fileToRemove.listFiles();
            if (filesToRemove != null) {
                for (File file : filesToRemove) {
                    try {
                        String[] toRemove = new String[2];
                        toRemove[0] = args[0];
                        toRemove[1] = file.getPath();
                        this.execute(toRemove);
                    } catch (Exception e) {
                        throw new Exception("\'" + file.getCanonicalPath()
                                + "\' : File cannot be removed: " + e.getMessage() + " ");
                    }
                }
            }
            if (!Files.deleteIfExists(pathToRemove)) {
                throw new Exception("\'" + fileToRemove.getCanonicalPath()
                        + "\' : File cannot be removed ");
            }
        }

        public Remove() {
            super(2);
        }
    }

    public static class Copy extends Command {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Path srcPath = Paths.get(args[1]);
            Path dstPath = Paths.get(args[2]);
            srcPath = parentShell.currentDir.toPath().resolve(srcPath).normalize();
            dstPath = parentShell.currentDir.toPath().resolve(dstPath).normalize();

            if (!Files.exists(srcPath)) {
                throw new Exception(args[1] + ": file not exist");
            }

            if (srcPath.equals(dstPath)) {
                throw new Exception("It's the same file");
            }

            if (Files.isDirectory(dstPath)) {
                dstPath = dstPath.resolve(srcPath.getFileName()).normalize();
            } else if (Files.isDirectory(srcPath) && Files.exists(dstPath)) {
                throw new Exception("File that isn\'t directory.");
            }

            if (dstPath.startsWith(srcPath)) {
                throw new Exception("Cannot move/copy file: cycle copy:"
                        + srcPath.toString() + " -> " + dstPath.toString());
            }
            Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            File[] sourceEntries = srcPath.toFile().listFiles();
            if (sourceEntries != null) {
                for (File entry : sourceEntries) {
                    String name = entry.getName();
                    String[] nw = new String[3];
                    nw[0] = args[0];
                    nw[1] = srcPath.resolve(name).normalize().toString();
                    nw[2] = dstPath.resolve(name).normalize().toString();
                    this.execute(nw);
                }
            }
        }
        public Copy() {
            super(3);
        }
    }

    public static class Move extends Command {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Path srcPath = Paths.get(args[1]);
            Path dstPath = Paths.get(args[2]);
            srcPath = parentShell.currentDir.toPath().resolve(srcPath).normalize();
            dstPath = parentShell.currentDir.toPath().resolve(dstPath).normalize();

            if (!Files.exists(srcPath)) {
                throw new Exception(args[1] + ": file not exist");
            }

            if (srcPath.equals(dstPath)) {
                throw new Exception("It's the same file");
            }

            if (Files.isDirectory(dstPath)) {
                dstPath = dstPath.resolve(srcPath.getFileName()).normalize();
            } else if (Files.isDirectory(srcPath) && Files.exists(dstPath)) {
                throw new Exception("File that isn\'t directory.");
            }

            if (dstPath.startsWith(srcPath)) {
                throw new Exception("Cannot move/copy file: cycle copy:"
                        + srcPath.toString() + " -> " + dstPath.toString());
            }
            Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            File[] sourceEntries = srcPath.toFile().listFiles();
            if (sourceEntries != null) {
                for (File entry : sourceEntries) {
                    String name = entry.getName();
                    String[] nw = new String[3];
                    nw[0] = args[0];
                    nw[1] = srcPath.resolve(name).normalize().toString();
                    nw[2] = dstPath.resolve(name).normalize().toString();
                    this.execute(nw);
                }
            }
        }

        public Move() {
            super(3);
        }
    }

    public static class Dir extends Command {
        @Override
        public void innerExecute(String[] args) {
            Path cwd = parentShell.currentDir.toPath();
            String[] buf = cwd.toFile().list();
            returnValue = buf;
        }

        public Dir() {
            super(1);
        }
    }

    public static class Exit extends Command {
        @Override
        public void innerExecute(String[] args) {
            System.exit(0);
        }

        public Exit() {
            super(1);
        }
    }
}
