package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Mods {
    public static void interactiveMode(Map<String, Command> commandMap, State state) throws ExitException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (scanner.hasNextLine()){
            String string = scanner.nextLine();
            try {
                parseCommands(string, commandMap, state);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
            System.out.print("$ ");
        }
    }

    public static void batchMode(String[] args, Map<String, Command> commandMap, State state) throws ExitException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : args) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }
        String commands = stringBuilder.toString();
        try {
            parseCommands(commands, commandMap, state);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void parseCommands(String commands, Map<String, Command> commandMap, State state) throws IOException, ExitException {
        /*String[] listOfCommand = commands.trim().split("\\s*;\\s*");
        for (String string : listOfCommand) {
            String[] commandArguments = string.split("\\s+");
            Command command = commandMap.get(commandArguments[0]);
            if (command == null) {
                throw new IOException("Wrong command name");
            }
            if (commandArguments.length != command.getArgumentsCount() + 1) {
                throw new IOException("Wrong argument count");
            } else {
                command.execute(state, commandArguments);
            }
        }*/

        String[] listOfCommand = commands.trim().split("\\s*;\\s*");
        for (String string : listOfCommand) {
            String[] commandArguments = string.split("\\s+", 3);
            Command command = commandMap.get(commandArguments[0]);

            if (command == null) {
                throw new IOException("Wrong command name");
            }
            if (commandArguments.length != command.getArgumentsCount() + 1) {
                throw new IOException("Wrong argument count");
            } else {
                command.execute(state, commandArguments);
            }
        }

    }
}
