package ru.fizteh.fivt.students.paulinMatavina;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ShellEnvironment {
    private File currentDir = new File(".");

    private int pwd() {
        try {
            System.out.println(currentDir.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("pwd: internal error: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    private String makeNewSource(final String source) {
        File newFile = new File(source);
        if (newFile.isAbsolute()) {
            return newFile.getAbsolutePath();
        } else {
            return currentDir.getAbsolutePath() + File.separator + source;
        }
    }

    private int cd(final String source) {
        File newDir = new File(makeNewSource(source));
        if (!newDir.exists() || !newDir.isDirectory()) {
            System.err.println("cd: " + source
                                 + ": is not a directory");
            return 1;
        }
        try {
            if (newDir.isAbsolute()) {
                currentDir = newDir;
            } else {
                currentDir = new File(currentDir.getCanonicalPath()
                            + File.separator + newDir);
            }
        } catch (Exception e) {
            System.err.println("cd: " + source
                    + ": is not a correct directory");
            return 1;
        }
        return 0;
    }

    private int makeDir(final String name) {
        if (name.equals("")) {
            System.err.println("mkdir: no directory name entered");
            return 1;
        }

        File dir = new File(makeNewSource(name));
        if (dir.exists()) {
            System.err.println("mkdir: directory already exists");
            return 1;
        }
        if (!dir.mkdir()) {
            System.err.println("mkdir: directory can't be created");
            return 1;
        }
        return 0;
    }

    private int moveCopyCheck(final File source, final File dest) {
        if (!source.exists()) {
            System.err.println("mv or cp: no such file");
            return 1;
        }
        if (dest.exists()) {
            System.err.println("mv or cp: writing on an existing file");
            return 1;
        }
        String canonicalSource = "";
        String parentDest = "";
        try {
            canonicalSource = source.getCanonicalPath();
            parentDest = dest.getParentFile().getCanonicalPath();
        } catch (IOException e) {
            System.err.println("mv or cp: internal error: " + e.getMessage());
            return 1;            
        }

        if (parentDest.startsWith(canonicalSource)) {
            System.err.println("mv or cp: attempt to move a folder into itself");
            return 1;   
        }
        return 0;
    }

    private int copy(final String sourceStr, final String destStr) {
        String newSourceStr = makeNewSource(sourceStr);
        String newDestStr = makeNewSource(destStr);
        File source = new File(newSourceStr);
        File dest = new File(newDestStr);
        if (dest.isDirectory()) {
            dest = new File(newDestStr + File.separator + source.getName());
        }
        try {
            if (dest.getCanonicalPath().equals(source.getCanonicalPath())) {
                System.err.println("copy: source path equals destination path");
                return 1;
            }
        } catch (IOException e) {
            System.err.println("copy: internal error: " + e.getMessage());
        }
        int check = moveCopyCheck(source, dest);
        if (check != 0) {
            return check;
        }
        try {
            Files.copy(source.toPath(), dest.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            if (source.isDirectory()) {
                String[] dirList = source.list();
                for (int i = 0; i < dirList.length; i++) {
                    copy(newSourceStr + File.separator + dirList[i],
                            dest.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            System.err.println("cp: error: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    private int move(final String sourceStr, final String destStr) {
        String newSourceStr = makeNewSource(sourceStr);
        String newDestStr = makeNewSource(destStr);
        File source = new File(newSourceStr);
        File dest = new File(newDestStr);
        if (dest.isDirectory()) {
            dest = new File(newDestStr + File.separator + source.getName());
        }
        try {
            if (dest.getCanonicalPath().equals(source.getCanonicalPath())) {
                System.err.println("move: source path equals destination path");
                return 1;
            }
        } catch (IOException e) {
            System.err.println("copy: internal error: " + e.getMessage());
        }
        int check = moveCopyCheck(source, dest);
        if (check != 0) {
            return check;
        }
        try {
            Files.move(source.toPath(), dest.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("mv: error: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    private int dir() {
        if (!currentDir.exists()) {
            System.err.println("dir: not found " + currentDir);
            return 1;
        }
        if (!currentDir.isDirectory()) {
            System.err.println("dir: " + currentDir + " is not a directory");
            return 1;
        }
        String[] currentDirList = currentDir.list();
        for (int i = 0; i < currentDirList.length; i++) {
            System.out.println(currentDirList[i]);
        }
        return 0;
    }

    private int remove(final String sourceStr) {
        try {
            File source = new File(makeNewSource(sourceStr));
            File[] dirList = source.listFiles();
            if (source.isDirectory()) {
                for (int i = 0; i < dirList.length; i++) {
                    remove(dirList[i].getAbsolutePath());
                }
            }
            if (!source.delete()) {
                System.err.println("rm: cannot delete: " + source);
                return 1;
            }
            return 0;
        } catch (Exception e) {
            System.err.println("rm: error: " + e.getMessage());
            return 1;
        }
    }

    private int execute(String query) {
        query = query.trim();
        if (query.equals("")) {
            return 0;
        }
        StringTokenizer token = new StringTokenizer(query);
        int tokenNum = token.countTokens();
        String command = token.nextToken();

        if (command.equals("exit") && tokenNum == 1) {
            System.exit(0);
        } else if (command.equals("cd") && tokenNum == 2) {
            String path = token.nextToken();
            return cd(path);
        } else if (command.equals("mkdir") && tokenNum == 2) {
            String dirname = token.nextToken();
            return makeDir(dirname);
        } else if (command.equals("pwd") && tokenNum == 1) {
            return pwd();
        } else if (command.equals("rm") && tokenNum == 2) {
            String file = token.nextToken();
            return remove(file);
        } else if (command.equals("mv") && tokenNum == 3) {
            String source = token.nextToken();
            String dest = token.nextToken();
            return move(source, dest);
        } else if (command.equals("cp") && tokenNum == 3) {
            String source = token.nextToken();
            String dest = token.nextToken();
            return copy(source, dest);
        } else if (command.equals("dir") && tokenNum == 1) {
            return dir();
        } else {
            System.err.println(command + ": wrong command format");
            return 1;
        }
        return 0;
    }
    
    public int executeQueryLine(String queryLine) {
        Scanner scanner = new Scanner(queryLine);
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String query = scanner.next();
            int status = execute(query);
            if (status != 0) {
                scanner.close();
                return status;
            }
        }
        scanner.close();
        return 0;
    }
}
