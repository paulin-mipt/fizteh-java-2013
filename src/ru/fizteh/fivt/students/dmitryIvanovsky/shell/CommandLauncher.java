package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Scanner;

import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.getMessage;

public class CommandLauncher {

    public enum Code {
        EXIT,
        OK,
        ERROR
    }

    CommandAbstract exampleClass;
    Map<String, String> mapCommand;
    Map<String, Method> commandMethod;
    Map<String, Boolean> mapSelfParsing;
    Map<String, Integer> countArgument;
    Boolean err = true;
    Boolean out = true;

    public CommandLauncher(CommandAbstract exampleClass) throws NoSuchMethodException {
        this.exampleClass = exampleClass;

        Map<String, Object[]> listCommand = exampleClass.mapComamnd();

        this.mapCommand = new HashMap<>();
        this.mapSelfParsing = new HashMap<>();
        this.countArgument = new HashMap<>();
        for (String key : listCommand.keySet()) {
            Object[] value = listCommand.get(key);
            mapCommand.put(key, (String) value[0]);
            mapSelfParsing.put(key, (Boolean) value[1]);
            countArgument.put(key, (Integer) value[2]);
        }

        commandMethod = new HashMap<String, Method>();
        Class[] paramTypes = new Class[]{String[].class};
        for (String key : mapCommand.keySet()) {
            try {
                commandMethod.put(key, exampleClass.getClass().getMethod(mapCommand.get(key), paramTypes));
            } catch (Exception e) {
                errPrint("Нет метода " + mapCommand.get(key));
                throw e;
            }
        }
    }

    public Code runCommand(String query, boolean isInteractiveMode) {
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();

        if (countTokens > 0) {
            String command = token.nextToken().toLowerCase();
            if (command.equals("exit") && countTokens == 1) {
                return Code.EXIT;
            } else if (mapCommand.containsKey(command)) {
                int countArg = countArgument.get(command);
                if (!mapSelfParsing.get(command) && (countArg + 1 != countTokens)) {
                    errPrint(String.format("%s: неверное число аргументов, нужно %d", command, countArg));
                    return Code.ERROR;
                }
                Method method = commandMethod.get(command);
                Vector<String> commandArgs = new Vector<>();
                for (int i = 2; i <= countTokens; ++i) {
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
                    return Code.OK;
                } catch (NullPointerException err) {
                    return Code.ERROR;
                } catch (Exception e) {
                    try {
                        if (e.getCause() != null) {
                            Exception newErr = (Exception) e.getCause();
                            if (newErr != null) {
                                getMessage(newErr);
                            }
                            return Code.ERROR;
                        }
                        return Code.ERROR;
                    } catch (Exception err) {
                        return Code.ERROR;
                    }
                }
            } else {
                errPrint("Неизвестная команда");
                return Code.ERROR;
            }
        } else {
            if (!isInteractiveMode) {
                errPrint("Пустой ввод");
            }
            return Code.ERROR;
        }
    }

    public Code runCommands(String query, boolean isInteractiveMode) {
        String[] command;
        command = query.split(";");
        for (String q : command) {
            Code res = runCommand(q, isInteractiveMode);
            if (res != Code.OK) {
                return res;
            }
        }
        return Code.OK;
    }

    public void interactiveMode() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.flush();
                System.out.println(exampleClass.startShellString());
                System.out.flush();
            } catch (Exception e) {
                errPrint("Неправильный путь");
                return;
            }
            if (sc.hasNextLine()) {
                String query = sc.nextLine();
                if (query.length() == 0) {
                    continue;
                }
                Code res = runCommands(query, true);
                if (res == Code.EXIT) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public Code runShell(String[] args) throws Exception {
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(' ');
            }
            String query = builder.toString();
            Code res = runCommands(query, false);
            return res;
        } else {
            interactiveMode();
            return Code.OK;
        }
    }

    private void errPrint(String message) {
        if (err) {
            System.err.flush();
            System.err.println(message);
            System.err.flush();
        }
    }

}
