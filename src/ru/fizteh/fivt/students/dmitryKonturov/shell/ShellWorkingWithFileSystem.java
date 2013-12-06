package ru.fizteh.fivt.students.dmitryKonturov.shell;

import java.io.File;
import java.nio.file.*;

public class ShellWorkingWithFileSystem extends ShellEmulator {

    private Path currentPath;


    private class ChangeDirectory implements ShellCommand {
        @Override
        public String getName() {
            return "cd";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 1) {
                throw new ShellException(getName(), "Too many arguments");
            } else if (args.length == 0) {
                throw new ShellException(getName(), "Lack of arguments");
            }
            try {
                Path tmpPath = currentPath.resolve(Paths.get(args[0])).normalize();
                if (Files.isDirectory(tmpPath)) {
                    currentPath = tmpPath;
                } else {
                    throw new ShellException(getName(), tmpPath.toString() + " is not directory");
                }
            } catch (ShellException se) {
                throw se;
            } catch (Exception e) {
                throw new ShellException(getName(), String.format("\'%s\' :Cannot convert to path", args[0]));
            }
        }
    }

    private class MakeDirectory implements ShellCommand {
        @Override
        public String getName() {
            return "mkdir";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 1) {
                throw new ShellException(getName(), "Too many arguments");
            } else if (args.length == 0) {
                throw new ShellException(getName(), "Lack of arguments");
            }
            try {
                Path tmpPath = currentPath.resolve(Paths.get(args[0]));
                Files.createDirectory(tmpPath);
            } catch (FileAlreadyExistsException e) {
                throw new ShellException(getName(), String.format("\'%s\' :File or directory already exists", args[0]));
            } catch (Exception e) {
                throw new ShellException(getName(), "Cannot create directory: " + e.getMessage());
            }
        }
    }

    private class PrintWritingDirectory implements ShellCommand {
        @Override
        public String getName() {
            return "pwd";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length != 0) {
                throw new ShellException(getName(), "Too many arguments");
            }
            try {
                System.out.println(currentPath.toAbsolutePath().toString());
            } catch (Exception e) {
                throw new ShellException(getName(), "Some I/O problems: " + e.getMessage());
            }
        }
    }

    private class Remove implements ShellCommand {
        @Override
        public String getName() {
            return "rm";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 1) {
                throw new ShellException(getName(), "Too many arguments");
            } else if (args.length == 0) {
                throw new ShellException(getName(), "Lack of arguments");
            }
            try {
                Path tmpPath = currentPath.resolve(args[0]);
                if (!Files.exists(tmpPath)) {
                    throw new ShellException(getName(), "File not exists");
                }
                if (Files.isDirectory(tmpPath)) {
                    File[] entries = tmpPath.toFile().listFiles();
                    if (entries != null) {
                        for (File file : entries) {
                            execute(new String[]{file.getAbsolutePath()}, info);
                        }
                    }
                }
                Files.delete(tmpPath);
            } catch (ShellException sh) {
                throw sh;
            } catch (AccessDeniedException ade) {
                throw new ShellException(getName(), "Access Denied: " + ade.getMessage());
            } catch (Exception e) {
                throw new ShellException(getName(), e.getMessage());
            }
        }
    }

    private class ShowDirectory implements ShellCommand {
        @Override
        public String getName() {
            return "dir";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length != 0) {
                throw new ShellException(getName(), "Too many arguments");
            }
            try {
                File tmpFile = currentPath.toFile();
                File[] entries = tmpFile.listFiles();
                if (entries != null) {
                    for (File file : entries) {
                        System.out.println(file.getName());
                    }
                } else {
                    throw new ShellException(getName(), "Not a directory. Wow. Please change your working directory.");
                }
            } catch (ShellException se) {
                throw se;
            } catch (SecurityException ade) {
                throw new ShellException(getName(), "Access denied: " + ade.getMessage());
            } catch (Exception e) {
                throw new ShellException(getName(), "Cannot open directory");
            }
        }
    }

    private class CopyFile implements ShellCommand {
        @Override
        public String getName() {
            return "cp";
        }

        protected boolean toMove() {
            return false;
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 2) {
                throw new ShellException(getName(), "Too many arguments");
            } else if (args.length < 2) {
                throw new ShellException(getName(), "Lack of arguments");
            }
            try {
                Path sourcePath = currentPath.resolve(Paths.get(args[0])).normalize();
                Path destinationPath = currentPath.resolve(Paths.get(args[1])).normalize();

                if (!Files.exists(sourcePath)) {
                    throw new ShellException(getName(), "Source file or directory not exists");
                }

                if (Files.isDirectory(destinationPath)) {
                    destinationPath.resolve(sourcePath.getFileName());
                } else if (Files.isDirectory(sourcePath) && Files.exists(destinationPath)) {
                    throw new ShellException(getName(), "Cannot copy/move directory to existing file");
                }

                if (destinationPath.startsWith(sourcePath)) {
                    throw new ShellException(getName(), "Cyclic dependencies");
                }

                if (toMove()) {
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                }

                File[] sourceEntries = sourcePath.toFile().listFiles();
                if (sourceEntries != null) {
                    for (File entry : sourceEntries) {
                        String name = entry.getName();
                        execute(new String[]{sourcePath.resolve(name).normalize().toString(),
                                destinationPath.resolve(name).normalize().toString()}, info);
                    }
                }

            } catch (ShellException se) {
                throw se;
            } catch (Exception e) {
                throw new ShellException(getName(), "I/O or security problems" + e.getMessage());
            }
        }
    }

    private class MoveFile extends CopyFile {
        @Override
        public String getName() {
            return "mv";
        }

        @Override
        protected boolean toMove() {
            return true;
        }
    }

    private class ExitCommand implements ShellCommand {
        @Override
        public String getName() {
            return "exit";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length > 0) {
                throw new ShellException(getName(), "Too many arguments");
            }
            System.exit(0);
        }
    }


    @Override
    protected String getGreetingString() {
        return (currentPath.toString() + "$ ");
    }

    public ShellWorkingWithFileSystem(String userPath) {
        super(null);
        ShellCommand[] commandsList = new ShellCommand[]{new ChangeDirectory(),
                                                         new MakeDirectory(),
                                                         new PrintWritingDirectory(),
                                                         new Remove(),
                                                         new ShowDirectory(),
                                                         new CopyFile(),
                                                         new MoveFile(),
                                                         new ExitCommand()};
        addToCommandList(commandsList);
        currentPath = Paths.get(userPath);
    }

}
