package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class Shell {

    public Shell(Vector<String> commandName, Vector<CommandInterface> commandObj) {
        File currDir = new File("."); // init. current directory
        try {
            currentDirectory = currDir.getCanonicalPath();
        } catch (IOException e) {
            System.err.println("Can't get path of current directory");
            System.exit(1);
        }
        command = new HashMap();  // init. map of commands
        for (int i = 0; i < commandName.size(); ++i) {
            command.put(commandName.elementAt(i), commandObj.elementAt(i));
        }
    }

    private static String currentDirectory;

    private static HashMap<String, CommandInterface> command;

    public static void changeCurrentDirectory(String newCurrentDirectory) {
        currentDirectory = newCurrentDirectory;
    }

    public static String getCurrentDirectory() {
        return currentDirectory;
    }

    public static String mergeAll(String[] arr) {
        StringBuilder s = new StringBuilder();
        for (String anArr : arr) {
            s.append(anArr);
            s.append(" ");
        }
        return s.toString();
    }

    public static boolean isEmpty(String str) {
        str = str.trim();
        return str.isEmpty();
    }

    public static void interactiveMode() {
        invite();
        Scanner sc = new Scanner(System.in);
        String input = "";
        if (sc.hasNextLine()) {
            input = sc.nextLine();
        } else {
            System.exit(0);
        }
        while (!input.equals("exit")) {
            String[] s = input.split("\\s*;\\s*");
            for (String value : s) {
                if (isEmpty(value)) {
                    continue;
                }
                try {
                    Shell.run(value);
                } catch (IOException | IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            }
            invite();
            if (sc.hasNextLine()) {
                input = sc.nextLine();
            } else {
                System.exit(0);
            }
        }
        try {     // exit our programm
            Shell.run(input);
        } catch (IOException | IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void packageMode(String[] arr) {
        String expression = mergeAll(arr);
        String[] s = expression.split("\\s*;\\s*");
        for (String value : s) {
            if (isEmpty(value)) {
                continue;
            }
            try {
                Shell.run(value);
            } catch (IOException | IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static void invite() {
        System.out.print("$ ");
    }

    public static String getAbsPath(final String path) throws IOException {
        File newFile = new File(path);
        if (!newFile.isAbsolute()) {
            newFile = new File(Shell.getCurrentDirectory(), path);
        }
        try {
            return newFile.getCanonicalPath();
        } catch (IOException e) {
            throw new IOException("error: can't get canonical path");
        }
    }

    public static void run(String expression) throws IOException, RuntimeException {
        String newExpression = expression.trim();
        ArrayList<String> args = new ArrayList<>();
        args.add(newExpression);
        String cmdName;
        if (newExpression.indexOf(' ', 0) != -1) {
            cmdName = newExpression.substring(0, newExpression.indexOf(' ', 0));
        } else {
            cmdName = newExpression;
        }
        CommandInterface cmd = command.get(cmdName);
        if (cmd == null) {
            throw new IOException("wrong command " + cmdName);
        } else {
            cmd.execute(args);
        }
    }

}
