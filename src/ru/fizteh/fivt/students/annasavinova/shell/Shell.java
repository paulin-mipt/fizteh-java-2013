package ru.fizteh.fivt.students.annasavinova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Shell extends UserShell {
    public static String currPath = System.getProperty("user.dir");

    public static File createFile(String arg) {
        if (new File(arg).isAbsolute()) {
            return new File(arg);
        }
        return new File(currPath + File.separator + arg);
    }

    public void doMkdir(String arg) {
        File currFile = createFile(arg);
        if (currFile.exists()) {
            printError("mkdir: cannot create a directory '" + arg + "': such directory exists");
        } else if (!currFile.mkdirs()) {
            printError("mkdir cannot create a directory'" + arg + "'");
        }
    }

    public void doDelete(File currFile) {
        if (currFile.exists()) {
            if (!currFile.isDirectory() || currFile.listFiles().length == 0) {
                if (!currFile.delete()) {
                    printError("rm: cannot remove '" + currFile.getName() + "'");
                }
            } else {
                while (currFile.listFiles().length != 0) {
                    doDelete(currFile.listFiles()[0]);
                }
                if (!currFile.delete()) {
                    printError("rm: cannot remove '" + currFile.getName() + "'");
                }
            }
        }
    }

    public void doRm(String arg) {
        File currFile = createFile(arg);
        if (!currFile.exists()) {
            printError("rm: cannot remove '" + arg + "': No such file or directory");
        } else {
            doDelete(currFile);
        }
    }

    public void doCd(String arg) {
        File currFile = createFile(arg);
        if (currFile.exists()) {
            try {
                currPath = currFile.getCanonicalPath();
            } catch (IOException e) {
                printError("cd: '" + arg + "': No such file or directory");
            }
        } else {
            printError("cd: '" + arg + "': No such file or directory");
        }
    }

    public void doDir() {
        File currFile = new File(currPath);
        File[] list = currFile.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; ++i) {
                if (!list[i].isHidden()) {
                    System.out.println(list[i].getName());
                }
            }
        }
    }

    private boolean isParent(File sourse, File dest) {
        try {
            String destPath = dest.getParentFile().getCanonicalPath();
            String soursePath = sourse.getCanonicalPath() + File.separator;
            if (destPath.startsWith(soursePath)) {
                return true;
            }
        } catch (IOException e) {
            printError("Incorrect Path");
        }
        return false;
    }

    public void doCp(String[] args) {
        File currFile = createFile(args[1]);
        File tmpFile = createFile(args[2]);
        File destFile = createFile(args[2] + File.separator + args[1]);
        if (!currFile.exists()) {
            printError("cp: cannot copy: '" + args[1] + "': No such file or directory");
        } else {
            if (!tmpFile.isDirectory()) {
                destFile = tmpFile;
            }
            try {
                if (isParent(currFile, destFile)) {
                    printError("cp: cannot copy: '" + args[1] + "': Sourse is parent of destination");
                } else {
                    if (currFile.getCanonicalPath().equals(tmpFile.getCanonicalPath())) {
                        printError("cp: cannot copy: '" + args[1] + "': Sourse is the same as destination");
                    } else {
                        Files.copy(currFile.toPath(), destFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (IOException e1) {
                printError("cp: cannot copy '" + args[1] + "'");
            }

        }
    }

    public void doMv(String[] args) {
        File currFile = createFile(args[1]);
        File tmpFile = createFile(args[2]);
        File destFile = createFile(args[2] + File.separator + args[1]);
        if (!currFile.exists()) {
            printError("mv: cannot move: '" + args[1] + "': No such file or directory");
        } else {
            if (!tmpFile.isDirectory()) {
                destFile = tmpFile;
            }
            try {
                if (isParent(currFile, destFile)) {
                    printError("mv: cannot move: '" + args[1] + "': Sourse is parent of destination");
                } else {
                    if (currFile.getCanonicalPath().equals(tmpFile.getCanonicalPath())) {
                        printError("mv: cannot move: '" + args[1] + "': Sourse is the same as destination");
                    } else {
                        Files.move(currFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (IOException e1) {
                printError("mv: cannot move '" + args[1] + "'");
            }

        }
    }

    protected void execProc(String[] args) {
        if (args != null && args.length != 0) {
            switch (args[0]) {
            case "pwd":
                if (checkArgs(1, args)) {
                    System.out.println(currPath);
                }
                break;
            case "cd":
                if (checkArgs(2, args)) {
                    doCd(args[1]);
                }
                break;
            case "dir":
                if (checkArgs(1, args)) {
                    doDir();
                }
                break;
            case "mkdir":
                if (checkArgs(2, args)) {
                    doMkdir(args[1]);
                }
                break;
            case "rm":
                if (checkArgs(2, args)) {
                    doRm(args[1]);
                }
                break;
            case "mv":
                if (checkArgs(3, args)) {
                    doMv(args);
                }
                break;
            case "cp":
                if (checkArgs(3, args)) {
                    doCp(args);
                }
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                printError("Unknown command");
            }
        }
    }

    public static void main(String[] args) {
        Shell sh = new Shell();
        sh.exec(args);
    }
}
