package ru.fizteh.fivt.students.dmitryKonturov.shell;

import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public abstract class ShellEmulator {

    private HashMap<String, ShellCommand> mapCommand = new HashMap<>();
    private final ShellInfo shellInfo;

    protected ShellEmulator(ShellInfo info) {
        shellInfo = info;
    }

    protected void addToCommandList(ShellCommand[] commandList) {
        //mapCommand.clear();
        for (ShellCommand command : commandList) {
            mapCommand.put(command.getName(), command);
        }
    }

    protected String getGreetingString() {
        return "$ ";
    }

    protected String[] shellParseArguments(String bigArg) {
        StringTokenizer tokenizer = new StringTokenizer(bigArg.trim());
        int argNum = tokenizer.countTokens();
        String[] args = new String[argNum];
        for (int i = 0; i < argNum; ++i) {
            args[i] = tokenizer.nextToken();
        }
        return args;
    }

    protected void executeCommand(String query) throws ShellException {
        String commandName;
        String[] arguments;
        try {
            String[] commandAndArgument = query.trim().split("\\s", 2);
            commandName = "";
            arguments = new String[0];
            if (commandAndArgument.length > 0) {
                commandName = commandAndArgument[0];
            }

            if (!(commandName.length() > 0)) {
                return; //empty command
            }

            if (commandAndArgument.length > 1) {
                arguments = shellParseArguments(commandAndArgument[1]);
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }

        ShellCommand currentCommand = mapCommand.get(commandName);
        if (currentCommand == null) {
            throw new ShellException(commandName, "No such command");
        }

        try {
            currentCommand.execute(arguments, shellInfo);
        } catch (Exception e) {
            throw new ShellException("Command " + commandName + " failed", e);
        }
    }

    private static StringBuilder getNiceMessageBuilder(Throwable e) {
        StringBuilder builder = new StringBuilder();
        String message = e.getMessage();
        if (message != null) {
            builder.append(message);
        }
        if (e.getCause() != null) {
            StringBuilder nextThrowable = getNiceMessageBuilder(e.getCause());
            if (nextThrowable.length() > 0) {
                if (builder.length() > 0) {
                    builder.append(": ");
                }
                builder.append(nextThrowable);
            }
        }

        return builder;
    }

    public static String getNiceMessage(Throwable e) {
        return getNiceMessageBuilder(e).toString();
    }

    private void executeQuery(String query) throws ShellException {
        Scanner scanner = new Scanner(query.trim());
        scanner.useDelimiter(";");
        try {
            while (scanner.hasNext()) {
                String commandWithArgs = scanner.next();
                executeCommand(commandWithArgs);
            }
        } catch (IllegalStateException e) {
            throw new ShellException("executeQuery", "Scanner is closed.");
        }
    }

    private void printGreeting() {
        String greeting = getGreetingString();
        System.out.print(greeting);
        System.out.flush();
    }

    public void packageMode(String query) throws ShellException {
        executeQuery(query);
    }

    public void interactiveMode() {
        Scanner sc = new Scanner(System.in);

        printGreeting();
        while (sc.hasNextLine()) {
            String query = sc.nextLine();
            try {
                executeQuery(query);
            } catch (ShellException sh) {
                System.err.println("Shell: " + getNiceMessage(sh));
            } catch (Exception e) {
                System.err.println("Shell: Unhandled exception: " + getNiceMessage(e));
            } finally {
                printGreeting();
            }
        }
    }

}
