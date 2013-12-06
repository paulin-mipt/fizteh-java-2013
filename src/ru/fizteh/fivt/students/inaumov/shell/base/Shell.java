package ru.fizteh.fivt.students.inaumov.shell.base;

import ru.fizteh.fivt.students.inaumov.shell.base.Command;
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

    private String[] args = new String[0];

    public void setState(State state) {
        this.state = state;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

	public void addCommand(Command command) {
		commandsMap.put(command.getName(), command);
	}

	public Command getCommand(String commandName) {
		Command command = commandsMap.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException(commandName + ": command not found");
        }

		return command;
	}

	public static String[][] parseString(String commandLine) {
        if (commandLine == null) {
            throw new IllegalArgumentException();
        }

		String[] args = commandLine.split("\\s*;\\s*");
		String[][] commands = new String[args.length][];

		for (int i = 0; i < args.length; ++i) {
			args[i] = args[i].trim();
			commands[i] = args[i].split("\\s+");
		}

		return commands;
	}

	public void executeAll(String[][] commands) throws UserInterruptionException {
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].length != 0) {
				Command command = getCommand(commands[i][0]);
                if (command.getArgumentsNumber() != commands[i].length - 1) {
                    throw new IllegalArgumentException(command.getName() + ": expected " + command.getArgumentsNumber() + " arguments, got " + (commands[i].length - 1) + " arguments");
                }

			    command.execute(commands[i], state);
			}
		}
	}

	private void batchMode() {
		StringBuilder stringBuilder = new StringBuilder();
		for (String nextEntry: args) {
			stringBuilder.append(nextEntry + " ");
		}

		String[][] commands = parseString(stringBuilder.toString());

		try {
			executeAll(commands);
		} catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
		} catch (UserInterruptionException exception) {
			System.exit(0);
		}
	}

	private void interactiveMode() {
		BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print(invite);

            String[][] commands = null;

            try {
			    commands = parseString(inputStreamReader.readLine());
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (IllegalArgumentException e) {
                System.exit(1);
            }

			try {
				executeAll(commands);
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