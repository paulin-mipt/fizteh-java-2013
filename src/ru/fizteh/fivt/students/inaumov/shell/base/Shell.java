package ru.fizteh.fivt.students.inaumov.shell.base;

import ru.fizteh.fivt.students.inaumov.shell.exceptions.UserInterruptionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Shell<State> {
    private final String invite = " $ ";

    private final Map<String, Command> commandsMap = new HashMap<String, Command>();
    private State state = null;

    private String[] args;

    public void setState(State state) {
        this.state = state;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void addCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("error: command is null");
        }

        commandsMap.put(command.getName(), command);
    }

    public Command getCommand(String commandName) {
        if (commandName == null || commandName.trim().isEmpty()) {
            throw new IllegalArgumentException("error: command name is null (or empty)");
        }

        Command command = commandsMap.get(commandName);

        return command;
    }

    private String[] parseCommandLine(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        inputLine = inputLine.trim();

        String commandName = null;
        String commandParameters = null;

        int spaceFirstEntryIndex = inputLine.indexOf(' ');
        if (spaceFirstEntryIndex == -1) {
            commandName = inputLine;
        } else {
            commandName = inputLine.substring(0, spaceFirstEntryIndex).trim();
            commandParameters = inputLine.substring(spaceFirstEntryIndex).trim();
        }

        return new String[]{commandName, commandParameters};
    }

    private String[][] parseBatchLine(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        inputLine = inputLine.trim();

        String[] commandsWithParameters = inputLine.split("\\s*;\\s*");
        String[][] commands = new String[commandsWithParameters.length][2];

        for (int i = 0; i < commandsWithParameters.length; ++i) {
            commands[i] = parseCommandLine(commandsWithParameters[i]);
        }

        return commands;
    }

    public static String[] parseCommandParameters(String commandParametersLine) {
        if (commandParametersLine == null || commandParametersLine.trim().isEmpty()) {
            return new String[0];
        }

        commandParametersLine = commandParametersLine.trim();
        String[] arguments = commandParametersLine.split("\\s+");

        for (int i = 0; i < arguments.length; ++i) {
            arguments[i] = arguments[i].trim();
        }

        return arguments;
    }

    public void executeAll(String[][] commands) throws UserInterruptionException {
        for (int i = 0; i < commands.length; ++i) {
                Command command = getCommand(commands[i][0]);
                if (command == null) {
                    throw new IllegalArgumentException("error: command " + commands[i][0] + " not found");
                }

                command.execute(commands[i][1], state);
        }
    }

    private void batchMode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String nextEntry: args) {
            stringBuilder.append(nextEntry + " ");
        }

        String[][] commands = parseBatchLine(stringBuilder.toString());

        try {
            executeAll(commands);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (UserInterruptionException e) {
            System.exit(0);
        }
    }

    private void interactiveMode() {
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print(invite);
            String[] nextEntry = null;

            try {
                nextEntry = parseCommandLine(inputStreamReader.readLine());
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (IllegalArgumentException e) {
                System.exit(1);
            }

            String[][] singleCommand = new String[1][];
            singleCommand[0] = nextEntry;

            try {
                executeAll(singleCommand);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
            } catch (UserInterruptionException e) {
                System.exit(0);
            }
        }
    }

    public void run() {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode();
        }
    }
}
