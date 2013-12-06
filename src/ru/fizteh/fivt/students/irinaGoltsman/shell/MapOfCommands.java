package ru.fizteh.fivt.students.irinaGoltsman.shell;

import java.util.HashMap;
import java.util.Map;

public class MapOfCommands {
    private static Map<String, Command> commands = new HashMap<String, Command>();

    public void addCommand(Command command) {
        if (!commands.containsKey(command.getName())) {
            commands.put(command.getName(), command);
        }
    }

    public static String[] splitCommand(String command) {
        StringBuilder str = new StringBuilder(command);
        while (str.charAt(0) == ' ' || str.charAt(0) == '\t') {
            str.delete(0, 1);
        }
        int currentChar = 0;
        while (currentChar < str.length() && str.charAt(currentChar) != ' ' && str.charAt(currentChar) != '\t') {
            currentChar++;
        }
        String nameOfCommand = str.substring(0, currentChar);
        str.delete(0, currentChar);
        while (str.length() != 0 && (str.charAt(0) == ' ' || str.charAt(0) == '\t')) {
            str.delete(0, 1);
        }
        if (str.length() == 0) {
            String[] result = new String[1];
            result[0] = nameOfCommand;
            return result;
        }
        currentChar = 0;
        while (currentChar < str.length() && str.charAt(currentChar) != ' ' && str.charAt(currentChar) != '\t') {
            currentChar++;
        }
        String firstArgument = str.substring(0, currentChar);
        str.delete(0, currentChar);
        while (str.length() != 0 && (str.charAt(0) == ' ' || str.charAt(0) == '\t')) {
            str.delete(0, 1);
        }
        if (str.length() == 0) {
            String[] result = new String[2];
            result[0] = nameOfCommand;
            result[1] = firstArgument;
            return result;
        }
        currentChar = str.length() - 1;
        while (str.length() != 0 && (str.charAt(currentChar) == ' ' || str.charAt(currentChar) == '\t')) {
            str.delete(currentChar, currentChar + 1);
            currentChar = str.length() - 1;
        }
        String secondArgument = str.toString();
        String[] result = new String[3];
        result[0] = nameOfCommand;
        result[1] = firstArgument;
        result[2] = secondArgument;
        return result;
    }

    public static Code commandProcessing(String command) {
        String[] partsOfCommand = splitCommand(command);
        String nameOfCommand = partsOfCommand[0];
        if (!commands.containsKey(nameOfCommand)) {
            System.err.println("Command '" + nameOfCommand + "' is not available or does not exist");
            return Code.ERROR;
        } else {
            if (commands.get(nameOfCommand).check(partsOfCommand)) {
                return commands.get(nameOfCommand).perform(partsOfCommand);
            } else {
                System.err.println("Command '" + nameOfCommand + "' has wrong arguments");
                return Code.ERROR;
            }
        }
    }
}
