package ru.fizteh.fivt.students.dobrinevski.shell;

import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Scanner;

public class Shell {
    public File currentDir;
    private static HashMap<String, Command> cmdMap;

    public Shell(HashMap<String, Command> ccmdMap, String dir) {
        currentDir = new File(System.getProperty(dir));
        cmdMap = ccmdMap;
        for (Command cmd : cmdMap.values()) {
            cmd.parentShell = this;
        }
    }

    public void removeFile(String[] args) throws Exception {
        Path pathToRemove = currentDir.toPath().resolve(args[1]).normalize();
        if (!Files.exists(pathToRemove)) {
            throw new Exception("Cannot be removed: File does not exist");
        }
        if (currentDir.toPath().normalize().startsWith(pathToRemove)) {
            throw new Exception("\'" + args[1]
                    + "\': Cannot be removed: First of all, leave this directory");
        }

        File fileToRemove = new File(args[1]);
        if (!fileToRemove.isAbsolute()) {
            fileToRemove = new File(currentDir.getCanonicalPath() + File.separator + args[1]);
        }
        File[] filesToRemove = fileToRemove.listFiles();
        if (filesToRemove != null) {
            for (File file : filesToRemove) {
                try {
                    String[] toRemove = new String[2];
                    toRemove[0] = args[0];
                    toRemove[1] = file.getPath();
                    removeFile(toRemove);
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

    private void executeCommand(String command) throws Exception {
        if (command.trim().isEmpty()) {
            return;
        }
        String[] args = command.trim().split("[\t ]+");
        Command buf = cmdMap.get(args[0]);
        if (args[0].equals("put")) {
            args = new String[2];
            args[0] = "put";
            args[1] = command.substring(4);
        }
        if (buf != null) {
            buf.execute(args);
            for (String s: buf.returnValue) {
                System.out.println(s);
            }
            buf.returnValue = null;
        } else {
            throw new Exception("No such command");
        }
    }

    public void executeCommands(String cmds) throws Exception {
        Scanner scanner = new Scanner(cmds);
        try {
            while (scanner.hasNextLine()) {
                String[] commands = scanner.nextLine().split(";");
                for (String cmd : commands) {
                    executeCommand(cmd);
                }
            }
        } finally {
            scanner.close();
        }
    }

    public void iMode() {
        Scanner scan = new Scanner(System.in);
        String greeting;

        try {
            greeting = currentDir.getCanonicalPath() + "$ ";
        } catch (Exception e) {
            greeting = "$ ";
        }
        System.out.print(greeting);
        System.out.flush();
        while (scan.hasNextLine()) {
            String commands = scan.nextLine().trim();
            if (commands.length() == 0) {
                System.out.print(greeting);
                System.out.flush();
                continue;
            }
            try {
                executeCommands(commands);
            } catch (Exception e) {
                System.out.println(e);
            }
            try {
                greeting = currentDir.getCanonicalPath() + "$ ";
            } catch (Exception e) {
                greeting = "$ ";
            }
            System.out.print(greeting);
            System.out.flush();
        }
    }
}
