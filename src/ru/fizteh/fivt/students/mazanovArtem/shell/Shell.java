package ru.fizteh.fivt.students.mazanovArtem.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Shell {
    public static void main(String[] args) {
        MyShell shell = new MyShell();
        if (args.length > 0) {
            StringBuilder action = new StringBuilder();
            for (String arg : args) {
                action.append(arg);
                action.append(' ');
            }
            try {
                shell.executeQuery(action.toString());
            } catch (ShellException a) {
                System.err.println(a);
                System.exit(1);
            }
        } else {
            shell.interactiveMode();
        }
    }
}

class ShellException extends Exception {
    private final String action;
    private final String message;

    ShellException(String act, String mes) {
        action = act;
        message = mes;
    }

    @Override
    public String toString() {
        return action + ": " + message;
    }

}

class MyShell {

    private File curDir;

    MyShell() {
        curDir = new File(System.getProperty("user.dir"));
    }

    private String appendPath(String path) {
        File tmp = new File(path);
        if (tmp.isAbsolute()) {
            return tmp.getAbsolutePath();
        } else {
            return curDir.getAbsolutePath() + File.separator + path;
        }
    }

    private void checkAmountArgs(String command, int actual, int need) throws ShellException {
        if (actual < need) {
            throw new ShellException(command, "Few arguments");
        } else if (actual > need) {
            throw new ShellException(command, "Too many arguments");
        }
    }

    private void changeDir(String[] args) throws ShellException {
        try {
            checkAmountArgs("cd", args.length, 1);
            File tmpFile = new File(appendPath(args[0]));
            if (tmpFile.exists()) {
                if (tmpFile.isDirectory()) {
                    curDir = tmpFile;
                } else {
                    throw new ShellException("cd", String.format("\'%s': Not directory", args[0]));
                }
            } else {
                throw new ShellException("cd", String.format("\'%s': No such file or directory", args[0]));
            }
        } catch (ShellException a) {
            throw a;
        } catch (Exception b) {
            throw new ShellException("cd", b.getMessage());
        }
    }

    private void mkdir(String[] args) throws ShellException {
        try {
            checkAmountArgs("mkdir", args.length, 1);
            File tmpFile = new File(appendPath(args[0]));
            if (tmpFile.exists()) {
                throw new ShellException("mkdir", String.format("\'%s': File or directory already exists", args[0]));
            } else {
                if (!tmpFile.mkdir()) {
                    throw new ShellException("mkdir", String.format("\'%s': Directory can't be create", args[0]));
                }
            }
        } catch (ShellException a) {
            throw a;
        } catch (Exception b) {
            throw new ShellException("mkdir", b.getMessage());
        }
    }

    private void pwd(String[] args) throws ShellException {
        try {
            checkAmountArgs("pwd", args.length, 0);
            System.out.println(curDir.getCanonicalPath());
        } catch (ShellException a) {
            throw a;
        } catch (Exception b) {
            throw new ShellException("pwd", b.getMessage());
        }
    }

    private void remove(String[] args) throws ShellException {
        try {
            checkAmountArgs("rm", args.length, 1);
            Path removePath = curDir.toPath().resolve(args[0]).normalize();
            if (curDir.toPath().normalize().startsWith(removePath)) {
                throw new ShellException("rm", String.format("\'%s\': "
                                         + "Can't be removed: leave this directory", args[0]));
            }
            if (!Files.exists(removePath)) {
                throw new ShellException("rm", "Can't be removed: File not exist");
            }
            File removeFile = new File(appendPath(args[0]));
            File[] removeFiles = removeFile.listFiles();
            if (removeFiles != null) {
                for (File file : removeFiles) {
                    try {
                        String[] toRemove = new String[1];
                        toRemove[0] = file.getPath();
                        remove(toRemove);
                    } catch (Exception a) {
                        throw new ShellException("rm", String.format("\'%s\': File can't be removed: %s",
                                file.getCanonicalPath(), a.getMessage()));
                    }
                }
            }
            try {
                if (!Files.deleteIfExists(removePath)) {
                    throw new ShellException("rm", String.format("\'%s\': File can't be removed",
                            removeFile.getCanonicalPath()));
                }
            } catch (DirectoryNotEmptyException b) {
                throw new ShellException("rm", String.format("\'%s\' : Directory isn't empty",
                        removeFile.getCanonicalPath()));
            }
        } catch (ShellException a) {
            throw a;
        } catch (AccessDeniedException c) {
            throw new ShellException("rm", "Access denied");
        } catch (Exception b) {
            throw new ShellException("rm", b.getMessage());
        }
    }

    private void dir(String[] args) throws ShellException {
        try {
            checkAmountArgs("dir", args.length, 0);
            File[] files = curDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getCanonicalFile().getName());
                }
            }
        } catch (ShellException a) {
            throw a;
        } catch (Exception b) {
            throw new ShellException("dir", b.getMessage());
        }
    }

    private void copy(String source, String target) throws ShellException {
        try {
            Path currentDir = Paths.get(curDir.getCanonicalPath());
            Path sourcePath = currentDir.resolve(source).normalize();
            Path targetPath = currentDir.resolve(target).normalize();

            if (!Files.exists(sourcePath)) {
                throw new ShellException("cp", String.format("%s file not exist", source));
            }
            if (Files.isDirectory(targetPath)) {
                targetPath = targetPath.resolve(sourcePath.getFileName()).normalize();
            } else {
                throw new ShellException("cp", "Can't copy directory");
            }
            if (sourcePath.equals(targetPath)) {
                throw new ShellException("cp", "File already exist");
            }
            if (targetPath.startsWith(sourcePath)) {
                throw new ShellException("cp", String.format("Can't copy %s to %s,because will cycle",
                        sourcePath.toString(), targetPath.toString()));
            }
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            File[] sourceFiles = sourcePath.toFile().listFiles();
            if (sourceFiles != null) {
                for (File file : sourceFiles) {
                    String name = file.getName();
                    copy(sourcePath.resolve(name).normalize().toString(),
                            targetPath.resolve(name).normalize().toString());
                }
            }
        } catch (ShellException a) {
            throw a;
        } catch (Exception b) {
            throw new ShellException("cp", b.getMessage());
        }
    }

    private void move(String source, String target) throws ShellException {
        try {
            Path currentDir = Paths.get(curDir.getCanonicalPath());

            Path sourcePath = currentDir.resolve(source).normalize();

            Path targetPath = currentDir.resolve(target).normalize();

            if (!Files.exists(sourcePath)) {
                throw new ShellException("mv", String.format("%s file not exist", source));
            }
            if (Files.isDirectory(targetPath)) {
                targetPath = targetPath.resolve(sourcePath.getFileName()).normalize();
            } else {
                throw new ShellException("mv", "Can't move directory");
            }
            if (sourcePath.equals(targetPath)) {
                throw new ShellException("mv", "File already exist");
            }
            if (targetPath.startsWith(sourcePath)) {
                throw new ShellException("mv", String.format("Can't move %s to %s,because will cycle",
                        sourcePath.toString(), targetPath.toString()));
            }
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            File[] sourceFiles = sourcePath.toFile().listFiles();
            if (sourceFiles != null) {
                for (File file : sourceFiles) {
                    String name = file.getName();
                    copy(sourcePath.resolve(name).normalize().toString(),
                            targetPath.resolve(name).normalize().toString());
                }
            }
        } catch (ShellException a) {
            throw a;
        } catch (Exception b) {
            throw new ShellException("mv", b.getMessage());
        }
    }

    private void executeCommand(String com) throws ShellException {
        StringTokenizer tokenizer = new StringTokenizer(com);
        int countArgs = tokenizer.countTokens();
        if (countArgs == 0) {
            return;
        }
        String command = tokenizer.nextToken();
        --countArgs;
        String[] args = new String[countArgs];
        for (int i = 0; i < countArgs; ++i) {
            args[i] = tokenizer.nextToken();
        }
        switch (command) {
            case "cd":
                changeDir(args);
                break;
            case "mkdir":
                mkdir(args);
                break;
            case "pwd":
                pwd(args);
                break;
            case "rm":
                remove(args);
                break;
            case "cp":
                checkAmountArgs("cp", args.length, 2);
                copy(args[0], args[1]);
                break;
            case "mv":
                checkAmountArgs("mv", args.length, 2);
                move(args[0], args[1]);
                break;
            case "dir":
                dir(args);
                break;
            case "exit":
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                throw new ShellException("Shell", "Unknown command");
        }
    }

    public void executeQuery(String query) throws ShellException {
        Scanner scanner = new Scanner(query);
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String com = scanner.next();
            executeCommand(com);
        }
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        String begin;
        try {
            begin = curDir.getCanonicalPath() + "$ ";
        } catch (Exception a) {
            begin = "$ ";
        }
        System.out.print(begin);
        System.out.flush();
        while (scanner.hasNextLine()) {
            String query = scanner.nextLine();
            query = query.trim();
            if (query.length() == 0) {
                System.out.print(begin);
                System.out.flush();
                continue;
            }
            try {
                executeQuery(query);
            } catch (ShellException a) {
                System.out.println(a);
            }
            try {
                curDir = curDir.getCanonicalFile();
                if (!Files.isDirectory(curDir.toPath())) {
                    System.err.println("This directory doesn't exist,return to default.Sorry");
                    curDir = new File(System.getProperty("user.dir"));
                }
                begin = curDir.getCanonicalPath() + "$ ";
            } catch (Exception a) {
                begin = "$ ";
            }
            System.out.print(begin);
            System.out.flush();
        }
    }
}
