package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.io.IOException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class ShellCommands {
    public static class Cd extends ShellCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Path newPath = Paths.get(args[1]);
            newPath = parentShell.getAbsolutePath(newPath);
            if (newPath.toFile().isDirectory()) {
                parentShell.setWorkingDir(newPath);
            } else {
                throw new Exception("No such file or directory");
            }
        }
        
        public Cd() {
            super(2);
        }
    }
    
    public static class Mkdir extends ShellCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Path newPath = Paths.get(args[1]);
            newPath = parentShell.getAbsolutePath(newPath);
            if (!newPath.toFile().mkdir()) {
                throw new Exception("Unable to create a directory");
            }
        }
        
        public Mkdir() {
            super(2);
        }
    }
    
    public static class Pwd extends ShellCommand {
        @Override
        public void innerExecute(String[] args) {
            parentShell.out.println(parentShell.getWorkingDir());
        }
        
        public Pwd() {
            super(1);
        }
    }
    
    public static class Rm extends ShellCommand {
        @Override
        public void innerExecute(String[] args) throws IOException, Exception {
            Path path = Paths.get(args[1]);
            path = parentShell.getAbsolutePath(path);
            if (path.toFile().isFile()) {
                Files.delete(path);
            } else {
                if (Shell.isSubdirectory(parentShell.getWorkingDir(), path) 
                        || parentShell.getWorkingDir().equals(path)) {
                    throw new Exception("Unable to remove: directory in use");
                }
                ShellUtility.removeDir(path);
            }
        }
        
        public Rm() {
            super(2);
        }
    }
    
    public static class Cp extends ShellCommand {
        @Override
        public void innerExecute(String[] args) throws Exception, IOException {
            Path source = Paths.get(args[1]);
            Path destination = Paths.get(args[2]);
            source = parentShell.getAbsolutePath(source);
            destination = parentShell.getAbsolutePath(destination);

            if (source.toFile().isFile() && destination.toFile().isDirectory()) {
                Files.copy(source, destination.resolve(source.getFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
            } else if (source.toFile().isFile()) {
                if (source.equals(destination)) {
                    throw new Exception("Source equals destination");
                }
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } else if (source.toFile().isDirectory() 
                    && (destination.toFile().isDirectory() || !destination.toFile().exists())) {
                if (!destination.toFile().exists()) {
                    destination.toFile().mkdir();
                }
                if (Shell.isSubdirectory(destination, source) || destination.equals(source)) {
                    throw new FileSystemLoopException("Unable to modify subdirectory of source dir");
                }
                final Path src = source;
                final Path dst = destination;
                Files.walkFileTree(src, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs) throws IOException {
                                Path targetdir = dst.resolve(src.getParent().relativize(dir));
                                Files.copy(dir, targetdir, StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult visitFile(Path file,
                                    BasicFileAttributes attrs) throws IOException {
                                Files.copy(file, dst.resolve(src.getParent().relativize(file)),
                                        StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }

                        });
            } else if (!source.toFile().exists()) {
                throw new Exception("Source file does not exist");
            } else {
                throw new Exception("Will not overwrite a file with a directory");
            }
        }
        
        public Cp() {
            super(3);
        }
    }
    
    public static class Mv extends ShellCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Command cp = new Cp().setShell(parentShell);
            Command rm = new Rm().setShell(parentShell);
            cp.execute("cp", args[1], args[2]);
            rm.execute("rm", args[1]);
        }
        
        public Mv() {
            super(3);
        }
    }
    
    public static class Dir extends ShellCommand {
        @Override
        public void innerExecute(String[] args) {
            Path cwd = parentShell.getWorkingDir();
            for (String fileName : cwd.toFile().list()) {
                parentShell.out.println(fileName);
            }
        }
        
        public Dir() {
            super(1);
        }
    }
    
    public static class Exit extends ShellCommand {
        @Override
        public void innerExecute(String[] args) {
            parentShell.exit(0);
        }
        
        public Exit() {
            super(1);
        }
    }
}
