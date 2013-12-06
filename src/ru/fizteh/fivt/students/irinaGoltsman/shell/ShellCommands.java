package ru.fizteh.fivt.students.irinaGoltsman.shell;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

public class ShellCommands {
    //print working directory, печатает абсолютный путь к текущей директории +
    public static class PrintWorkDirectory implements Command {
        public final String name = "pwd";
        public final int countOfArguments = 0;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] parts) {
            Properties p = System.getProperties();
            String dir = p.getProperty("user.dir");
            System.out.println(dir);
            return (Code.OK);
        }
    }

    //Печатает содержимое текущей директории.
    public static class Dir implements Command {
        public final String name = "dir";
        public final int countOfArguments = 0;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] parts) {
            Properties p = System.getProperties();
            String dir = p.getProperty("user.dir");
            File f1 = new File(dir);
            try {
                for (String child : f1.list()) {
                    System.out.println(child);
                }
            } catch (Exception e) {
                System.err.println(e);
                return (Code.SYSTEM_ERROR);
            }
            return (Code.OK);
        }
    }

    //Создание новой директории в текущей директории.
    public static class MakeDir implements Command {
        public final String name = "mkdir";
        public final int countOfArguments = 1;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] parts) {
            String nameOfDir = parts[1];
            Properties p = System.getProperties();
            String dir = p.getProperty("user.dir");
            File f = new File(dir + File.separator + nameOfDir);
            if (!f.exists()) {
                boolean result = f.mkdir();
                if (!result) {
                    System.err.println("mkdir: dir '" + nameOfDir + "' can not be made");
                    return Code.ERROR;
                }
            } else {
                System.err.println("mkdir: dir '" + nameOfDir + "' already exists");
                return Code.ERROR;
            }
            return (Code.OK);
        }
    }

    //Копирует указанную в параметра папку/файл в указанное место.
    public static class Copy implements Command {
        public final String name = "cp";
        public final int countOfArguments = 2;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            if ((parts.length - 1) != countOfArguments) {
                return false;
            }
            String destination = parts[2];
            int numberOfWords = destination.split(" ").length;
            if (numberOfWords > 1) {
                return false;
            }
            numberOfWords = destination.split("\t").length;
            return (numberOfWords == 1);
        }

        @Override
        public Code perform(String[] parts) {
            String source = parts[1];
            String destination = parts[2];
            Properties p = System.getProperties();
            String userDir = p.getProperty("user.dir");
            File from = new File(userDir + File.separator + source);
            File to = new File(userDir + File.separator + destination);
            if (!from.exists()) {
                System.err.println("cp: '" + source + "': No such file or directory");
                return Code.ERROR;
            }
            if (!to.exists()) {
                try {
                    Files.copy(from.toPath(), to.toPath());
                } catch (Exception e) {
                    System.err.println(e);
                    return Code.SYSTEM_ERROR;
                }
                return Code.OK;
            }
            if (!to.isDirectory()) {
                System.err.println("cp: '" + destination + "': Is not a directory");
                return Code.ERROR;
            }
            File toInDir = new File(destination + File.separator + source);
            if (toInDir.exists()) {
                System.err.println("cp: '" + source + "': File with such name already exist in '" + destination + "'");
                return Code.ERROR;
            }
            try {
                Files.copy(from.toPath(), toInDir.toPath());
            } catch (Exception e) {
                System.err.println(e);
                return Code.SYSTEM_ERROR;
            }
            return Code.OK;
        }
    }

    // mv <source> <destination> — переносит указанный файл/папку в новое место (файл на прежнем месте удаляется).
    // В частности переименовывает файл/папку, если source и destination находятся в одной папке
    public static class Move implements Command {
        public final String name = "mv";
        public final int countOfArguments = 2;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            if ((parts.length - 1) != countOfArguments) {
                return false;
            }
            String destination = parts[2];
            int numberOfWords = destination.split(" ").length;
            if (numberOfWords > 1) {
                return false;
            }
            numberOfWords = destination.split("\t").length;
            return (numberOfWords == 1);
        }

        @Override
        public Code perform(String[] parts) {
            String source = parts[1];
            String destination = parts[2];
            Properties p = System.getProperties();
            String userDir = p.getProperty("user.dir");
            File from = new File(userDir + File.separator + source);
            if (!from.exists()) {
                System.err.println("wv: cannot move'" + source + "': No such file or directory'");
                return Code.ERROR;
            }
            if (destination.equals(".")) {
                return Code.OK;
            }
            File toDir = new File(userDir + File.separator + destination);
            if (!toDir.exists()) {
                if (!from.renameTo(toDir)) {
                    System.err.println("wv: cannot rename'" + source + "': to '" + destination + "'");
                    return Code.ERROR;
                } else {
                    return Code.OK;
                }
            } else {
                Copy cp = new Copy();
                Code cpReturnCode = cp.perform(parts);
                if (cpReturnCode != Code.OK) {
                    return cpReturnCode;
                }
                Remove rm = new Remove();
                return rm.perform(new String[]{source});
            }
        }
    }

    //Удаляет указанную в параметрах папку (рекурсивно) или файл.
    public static class Remove implements Command {
        public final String name = "rm";
        public final int countOfArguments = 1;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] parts) {
            String path = parts[1];
            try {
                Properties p = System.getProperties();
                String userDir = p.getProperty("user.dir");
                File inputFile = new File(path);
                if (!inputFile.isAbsolute()) {
                    inputFile = new File(userDir + File.separator + path);
                }
                if (!inputFile.exists()) {
                    throw new IllegalStateException(path + " not exists");
                }
                if (inputFile.isDirectory()) {
                    for (File childFile : inputFile.listFiles()) {
                        if (childFile != null) {
                            if (childFile.isDirectory()) {
                                ChangeDirectory cd = new ChangeDirectory();
                                cd.perform(new String[]{"cd ", path});
                                if (perform(new String[]{"rm ", childFile.toString()}) == Code.SYSTEM_ERROR) {
                                    return Code.SYSTEM_ERROR;
                                }
                                cd.perform(new String[]{"cd ", ".."});
                            } else if (!childFile.delete()) {
                                System.err.println("rm: impossible to remove file '" + childFile.toString() + "'.");
                                return Code.ERROR;
                            }
                        }
                    }
                }
                userDir = p.getProperty("user.dir");
                if (userDir.equals(inputFile)) {
                    ChangeDirectory cd = new ChangeDirectory();
                    cd.perform(new String[]{".."});
                }
                if (!inputFile.delete()) {
                    System.err.println("rm: impossible to remove file '" + path + "'.");
                    return Code.ERROR;
                } else {
                    return Code.OK;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return Code.SYSTEM_ERROR;
            }
        }
    }

    //Change directory, смена текущей директории. Поддерживаются ., .., относительные и абсолютные пути.
    public static class ChangeDirectory implements Command {
        public final String name = "cd";
        public final int countOfArguments = 1;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] parts) {
            String inputNameDir = parts[1];
            File newDir = new File(inputNameDir);
            String path = "";
            try {
                path = newDir.getCanonicalPath();
                newDir = new File(path);
            } catch (Exception e) {
                System.err.println(e);
                return (Code.SYSTEM_ERROR);
            }
            try {
                if (!newDir.exists()) {
                    System.err.println("cd: '" + inputNameDir + "': No such file or directory");
                    return (Code.ERROR);
                }
                if (!newDir.isDirectory()) {
                    System.err.println("cd: '" + inputNameDir + "': Is not a directory");
                    return (Code.ERROR);
                }
                System.setProperty("user.dir", path);
            } catch (Exception e) {
                System.err.println(e);
                return (Code.SYSTEM_ERROR);
            }
            return (Code.OK);
        }
    }

    public static class Exit implements Command {
        public final String name = "exit";
        public final int countOfArguments = 0;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] parts) {
            return Code.EXIT;
        }
    }
}
