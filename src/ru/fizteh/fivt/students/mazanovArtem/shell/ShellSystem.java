package ru.fizteh.fivt.students.mazanovArtem.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.StandardCopyOption;

public class ShellSystem implements Command {

    private static File curDir;

    public File getFile() {
        return curDir;
    }

    @Override
    public String helloString() throws IOException {
        return getFile().getCanonicalPath() + "$ ";
    }

    @Override
    public Map<String, Object[]> linkCommand() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("dir", new Object[] {"dir", false, 0 });
            put("mv", new Object[] {"mv", false, 2 });
            put("cp", new Object[] {"cp", false, 2 });
            put("rm", new Object[] {"rm", false, 1 });
            put("pwd", new Object[] {"pwd", false, 0 });
            put("mkdir", new Object[] {"mkdir", false, 1 });
            put("cd", new Object[] {"cd", false, 1 });
        }};
        return commandList;
    }

    public ShellSystem() {
        curDir = new File(".");
    }

    private static String appendPath(String path) {
        File tmp = new File(path);
        if (tmp.isAbsolute()) {
            return tmp.getAbsolutePath();
        } else {
            return curDir.getAbsolutePath() + File.separator + path;
        }
    }

    private static void checkAmountArgs(String command, int actual, int need) throws ShellException {
        if (actual < need) {
            throw new ShellException(command, "Few arguments");
        } else if (actual > need) {
            throw new ShellException(command, "Too many arguments");
        }
    }

    public void dir(String[] args) throws ShellException {
        checkAmountArgs("dir", args.length, 0);
        File[] files = curDir.listFiles();
        try {
            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getCanonicalFile().getName());
                }
            }
        } catch (Exception e) {
            throw new ShellException("dir", e.getMessage());
        }
    }

    public void pwd(String[] args) throws ShellException {
        checkAmountArgs("pwd", args.length, 0);
        try {
            curDir = curDir.getCanonicalFile();
            System.out.println(curDir);
        } catch (Exception e) {
            throw new ShellException("pwd", e.getMessage());
        }
    }

    public File cd(String[] args) throws ShellException {
        checkAmountArgs("cd", args.length, 1);
        File tmp = new File(appendPath(args[0]));
        if (tmp.exists()) {
            if (tmp.isDirectory()) {
                try {
                    curDir = tmp.getCanonicalFile();
                } catch (IOException e) {
                    System.out.println("Проблемы с путем");
                }
            } else {
                throw new ShellException("cd", args[0] + " isn't a directory");
            }
        } else {
            throw new ShellException("cd", args[0] + " doesn't exist");
        }
        return curDir;
    }

    public void mkdir(String[] args) throws ShellException {
        checkAmountArgs("mkdir", args.length, 1);
        File tmp = new File(appendPath(args[0]));
        if (tmp.exists()) {
            throw new ShellException("mkdir", args[0] + " is exist");
        } else {
            if (!tmp.mkdir()) {
                throw new ShellException("mkdir", args[0] + " : Directory can't be create");
            }
        }
    }

    public void rm(String[] args) throws ShellException {
        checkAmountArgs("rm", args.length, 1);
        File rmFile = new File(appendPath(args[0])).getAbsoluteFile();
        Path rmPath = rmFile.toPath();
        if (curDir.toPath().startsWith(rmPath)) {
            throw new ShellException("rm", args[0] + " can't be removed: leave this directory");
        }
        if (!rmFile.exists()) {
            throw new ShellException("rm", " directory/file doesn't exist");
        }
        File[] files = rmFile.listFiles();
        if (files != null) {
            for (File file : files) {
                String[] toRemove = new String[1];
                toRemove[0] = file.getPath();
                rm(toRemove);
            }
        }
        try {
            try {
                if (!Files.deleteIfExists(rmPath)) {
                    throw new ShellException("rm", rmFile.getCanonicalPath() + " : File can't be removed");
                }
            } catch (DirectoryNotEmptyException b) {
                throw new ShellException("rm", rmFile.getCanonicalPath() + " : Directory isn't empty");
            }
        } catch (ShellException a) {
            throw a;
        } catch (AccessDeniedException b) {
            throw new ShellException("rm", "Access denied");
        } catch (Exception c) {
            throw new ShellException("rm", c.getMessage());
        }
    }

    public void cp(String[] args) throws ShellException {
        String source = args[0];
        String target = args[1];
        cpmv(source, target, false);
    }

    public void mv(String[] args) throws ShellException {
        String source = args[0];
        String target = args[1];
        cpmv(source, target, true);
    }

    private static void cpmv(String source, String target, boolean flag) throws ShellException {
        String action;
        if (flag) {
            action = "mv";
        } else {
            action = "cp";
        }
        File sourceFile = new File(appendPath(source));
        File targetFile = new File(appendPath(target));
        targetFile = targetFile.getAbsoluteFile();
        sourceFile = sourceFile.getAbsoluteFile();
        Path sourcePath = sourceFile.toPath();
        Path targetPath = targetFile.toPath();
        if (!Files.exists(sourcePath)) {
            throw new ShellException(action, source + " : file not exist");
        }
        if (Files.isDirectory(targetPath)) {
            targetPath = targetPath.resolve(sourcePath.getFileName()).normalize();
        } else {
            throw new ShellException(action, "Can't move/copy directory");
        }
        if (sourcePath.equals(targetPath)) {
            throw new ShellException(action, "File already exist");
        }
        if (targetPath.startsWith(sourcePath)) {
            throw new ShellException(action, "Can't move/copy " + sourcePath.toString() + " to "
                    + targetPath.toString() + " because will cycle");
        }
        try {
            if (flag) {
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new ShellException(action, e.getMessage());
        }
        File[] sourceFiles = sourceFile.listFiles();
        if (sourceFiles != null) {
            for (File file : sourceFiles) {
                String name = file.getName();
                cpmv(appendPath(name), appendPath(name), flag);
            }
        }
    }

    public void exit() {

    }
}
