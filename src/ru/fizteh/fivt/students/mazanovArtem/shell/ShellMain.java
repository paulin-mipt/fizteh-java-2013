package ru.fizteh.fivt.students.mazanovArtem.shell;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Scanner;

public class ShellMain {

    Command exampleClass;
    Map<String, String> mapCommand;
    Map<String, Method> commandMethod;
    Map<String, Boolean> mapSelfParsing;
    Map<String, Integer> countNeedArgument;

    public ShellMain(Command exampleClass) throws NoSuchMethodException {
        this.exampleClass = exampleClass;

        Map<String, Object[]> commandLink = exampleClass.linkCommand();

        mapCommand = new HashMap<>();
        mapSelfParsing = new HashMap<>();
        countNeedArgument = new HashMap<>();
        for (String key : commandLink.keySet()) {
            Object[] value = commandLink.get(key);
            mapCommand.put(key, (String) value[0]);
            mapSelfParsing.put(key, (Boolean) value[1]);
            countNeedArgument.put(key, (Integer) value[2]);
        }

        commandMethod = new HashMap<>();
        Class[] paramTypes = new Class[]{String[].class};
        for (String key : mapCommand.keySet()) {
            try {
                commandMethod.put(key, exampleClass.getClass().getMethod(mapCommand.get(key), paramTypes));
            } catch (Exception e) {
                System.out.println("Нет метода " + mapCommand.get(key));
                throw e;
            }
        }
    }

    public int runCommand(String query, boolean isInteractiveMode) {
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();

        if (countTokens > 0) {
            String command = token.nextToken().toLowerCase();
            if (command.equals("exit") && countTokens == 1) {
                return -1;
            } else if (mapCommand.containsKey(command)) {
                int countArg = countNeedArgument.get(command);
                if (!mapSelfParsing.get(command) && (countArg + 1 != countTokens)) {
                    System.out.println(String.format("%s: неверное число аргументов, нужно %d", command, countArg));
                    return 1;
                }
                Method method = commandMethod.get(command);
                Vector<String> commandArgs = new Vector<>();
                for (int i = 1; i < countTokens; ++i) {
                    commandArgs.add(token.nextToken());
                }
                try {
                    if (mapSelfParsing.get(command)) {
                        Object[] args = new Object[]{new String[]{query}};
                        method.invoke(exampleClass, args);
                    } else {
                        Object[] args = new Object[]{commandArgs.toArray(new String[commandArgs.size()])};
                        method.invoke(exampleClass, args);
                    }
                    return 0;
                } catch (Exception e) {
                    System.out.println(e.getCause());
                    return 1;
                }
            } else {
                System.out.println("Неизвестная команда");
                return 1;
            }
        } else {
            if (!isInteractiveMode) {
                System.out.println("Пустой ввод");
            }
            return 1;
        }
    }

    public int executeQuery(String query, boolean isInteractiveMode) throws ShellException {
        Scanner scanner = new Scanner(query);
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String com = scanner.next();
            int res = runCommand(com, isInteractiveMode);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

    public int runShell(String[] args) throws Exception {
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(' ');
            }
            String query = builder.toString();
            int res = executeQuery(query, false);
            try {
                exampleClass.exit();
            } catch (Exception e) {
                System.out.println(e.getCause());
                throw e;
            }

            return res;

        } else {
            interactiveMode();
            try {
                exampleClass.exit();
            } catch (Exception e) {
                System.out.println(e.getCause());
                throw e;
            }

            return 0;

        }
    }

    public void interactiveMode() throws ShellException {
        try {
            System.out.print(exampleClass.helloString());
            System.out.flush();
        } catch (Exception e) {
            System.out.print("Проблемы с путем");
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String query = scanner.nextLine().trim();
            if (query.length() == 0) {
                continue;
            } else {
                int result = executeQuery(query, true);
                if (result == -1) {
                    return;
                }
            }
            try {
                System.out.print(exampleClass.helloString());
                System.out.flush();
            } catch (Exception e) {
                System.out.println("Плохой путь");
            }
        }
    }
}

