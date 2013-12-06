package ru.fizteh.fivt.students.mishatkin.shell;

import java.util.*;

/**
 * CommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 */
public abstract class CommandSource {
	private Map<String, Command<CommandReceiver>> validClasses = new HashMap<>();

	private ArrayList<String> commandsStringsBuffer = new ArrayList<>();
	private ArrayList<String> commandArgumentsBuffer = new ArrayList<>();

	public void clearBuffers() {
		commandArgumentsBuffer.clear();
		commandsStringsBuffer.clear();
	}

	public Command<CommandReceiver> nextCommand(CommandReceiver receiver) throws ShellException {
		if (commandsStringsBuffer.isEmpty()) {
			if (!hasMoreData()) {
				throw new TimeToExitException();
			}
			String line = nextLine();
			String[] commandsSequence = line.split(";");
			Collections.addAll(commandsStringsBuffer, commandsSequence);
		}
		Collections.addAll(commandArgumentsBuffer, commandsStringsBuffer.get(0).trim().split("\\s+"));
		commandsStringsBuffer.remove(0);

		// removing white spaces
		while (commandArgumentsBuffer.remove("")) {
		}
		Command<CommandReceiver> theCommand = null;
		try {
			theCommand = createCommand(receiver);
		} catch (IllegalArgumentException e) {
			commandsStringsBuffer.clear();
		}
		commandArgumentsBuffer.clear();
		return theCommand;
	}

	private Command<CommandReceiver> createCommand(CommandReceiver receiver) throws ShellException {
		if (commandArgumentsBuffer.isEmpty()) {
			return null;
		}
		Command<CommandReceiver> retValue = null;
		String commandName = commandArgumentsBuffer.get(0).toLowerCase();
		if (!validClasses.containsKey(commandName)) {
			throw new ShellException(commandName + ": invalid command.");
		}
		retValue = validClasses.get(commandName);
		retValue.setReceiver(receiver);
		if (retValue != null) {
			readArgs(retValue);
		}
		return retValue;
	}

	private void readArgs(Command<CommandReceiver> command) throws ShellException {
		if (command.getArgumentsCount() != commandArgumentsBuffer.size() - 1) {
			throw new ShellException("Invalid number of arguments for command \'" + command.getName() + "\'.");
		}
		String[] args = new String[command.getArgumentsCount()];
		for (int argumentIndex = 0; argumentIndex < command.getArgumentsCount(); ++argumentIndex) {
			args[argumentIndex] = commandArgumentsBuffer.get(argumentIndex + 1);
		}
		command.setArguments(args);
	}

	public <SomeCommandReceiver extends CommandReceiver> void initCommands(Collection<Command<SomeCommandReceiver>> commands) {
		Map<String, Command<CommandReceiver>> temporary = new HashMap<>();
		for (Command<SomeCommandReceiver> command : commands) {
			temporary.put(command.getName(), (Command<CommandReceiver>) command);
		}
		validClasses = Collections.unmodifiableMap(temporary);
	}

	public boolean hasUnexecutedCommands() {
		return !commandsStringsBuffer.isEmpty();
	}

	public abstract boolean hasMoreData();
	public abstract String nextLine();
}
