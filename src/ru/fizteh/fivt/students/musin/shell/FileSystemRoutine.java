package ru.fizteh.fivt.students.musin.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileSystemRoutine {
    private static Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("hello", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    System.out.println("hello to you too");
                    return 0;
                }
            }),
            new Shell.ShellCommand("dir", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() != 0) {
                        System.err.println("dir: Too many arguments");
                        return -1;
                    }
                    for (File f : shell.currentDirectory.listFiles()) {
                        System.out.println(f.getName());
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("pwd", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() != 0) {
                        System.err.println("pwd: Too many arguments");
                        return -1;
                    }
                    try {
                        System.out.println(shell.currentDirectory.getCanonicalPath());
                    } catch (Exception e) {
                        System.err.println("pwd: Current Folder Error");
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("mkdir", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("mkdir: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("mkdir: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = shell.currentDirectory.toPath();
                        path = path.resolve(args.get(0));
                        File newDir = path.toFile();
                        if (!newDir.mkdir()) {
                            System.err.printf("mkdir: Folder '%s' can not be created\n", args.get(0));
                            return -1;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("cd", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("cd: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("cd: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(shell.currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File newDir = new File(path.toAbsolutePath().toString());
                        if (!newDir.exists() || !newDir.isDirectory()) {
                            System.err.printf("cd: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            shell.currentDirectory = newDir;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("rm", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("rm: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("rm: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(shell.currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File newDir = new File(path.toAbsolutePath().toString());
                        if (!newDir.exists()) {
                            System.err.printf("rm: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            FileSystemRoutine.deleteDirectoryOrFile(newDir);
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("mv", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 2) {
                        System.err.println("mv: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        System.err.println("mv: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(shell.currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File from = new File(path.toAbsolutePath().toString());
                        if (!from.exists()) {
                            System.err.printf("mv: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            path = Paths.get(shell.currentDirectory.getCanonicalPath());
                            path = path.resolve(args.get(1));
                            File to = new File(path.toAbsolutePath().toString());
                            if (!from.renameTo(to)) {
                                System.err.println("mv: File or directory can't be moved");
                                return -1;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("cp", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 2) {
                        System.err.println("cp: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        System.err.println("cp: Too few arguments");
                        return -1;
                    }
                    try {
                        Path path = Paths.get(shell.currentDirectory.getCanonicalPath());
                        path = path.resolve(args.get(0));
                        File from = new File(path.toAbsolutePath().toString());
                        if (!from.exists()) {
                            System.err.printf("mv: '%s': No such file or directory\n", args.get(0));
                            return -1;
                        } else {
                            path = Paths.get(shell.currentDirectory.getCanonicalPath());
                            path = path.resolve(args.get(1));
                            File to = new File(path.toAbsolutePath().toString());
                            if (to.exists() && from.equals(to)) {
                                System.err.println("cp: It's the same file");
                                return -1;
                            }
                            if (to.exists() && to.isDirectory()) {
                                to = to.toPath().resolve(from.getName()).toFile();
                                FileSystemRoutine.copyDirectory(from, to);
                            } else {
                                FileSystemRoutine.copyDirectory(from, to);
                            }
                        }

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return -1;
                    }
                    return 0;
                }
            })
    };


    public static class FileList {
        File file;
        ArrayList<FileList> list;

        public FileList() {
            list = new ArrayList<FileList>();
        }
    }

    public static boolean deleteDirectoryOrFile(File file) {
        boolean result = true;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                result = result && deleteDirectoryOrFile(f);
            }
        }
        if (!file.delete()) {
            return false;
        }
        return result;
    }

    public static void getFileList(FileList fl) {
        if (fl.file.isDirectory()) {
            for (File f : fl.file.listFiles()) {
                FileList files = new FileList();
                files.file = f;
                if (f.isDirectory()) {
                    getFileList(files);
                }
                fl.list.add(files);
            }
        }
    }

    public static void copyFileList(FileList fl, File to) throws IOException {
        try {
            Files.copy(Paths.get(fl.file.getCanonicalPath()), Paths.get(to.getCanonicalPath()), new CopyOption[0]);
        } catch (FileAlreadyExistsException e) {
            //It Already there, no action needed
        }
        for (FileList list : fl.list) {
            File newDir = new File(to.getCanonicalPath() + "/" + list.file.getName());
            copyFileList(list, newDir);
        }
    }

    public static void copyDirectory(File from, File to) throws IOException {
        FileList fl = new FileList();
        fl.file = from;
        getFileList(fl);
        copyFileList(fl, to);
    }

    public static void integrate(Shell shell) {
        for (int i = 0; i < commands.length; i++) {
            shell.addCommand(commands[i]);
        }
    }
}
