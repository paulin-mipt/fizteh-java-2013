package ru.fizteh.fivt.students.visamsonov.shell;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Shell<T> {
	
	private final Map<String, Command> commandList = new HashMap<String, Command>();
	private final T state;

	public Shell (T state) {
		this.state = state;
	}

	public void addCommand (Command command) {
		commandList.put(command.getName(), command);
	}

	public boolean perform (String[] args) {
		ArgumentParser parser = new ArgumentParser(args);
		for (RawCommand command = parser.nextCommand(); command != null; command = parser.nextCommand()) {
			Command availableCommand = commandList.get(command.name);
			if (availableCommand == null) {
				System.err.printf("%s: command not found\n", command.name);
				return false;
			}
			if (!availableCommand.evaluate(state, command.args)) {
				return false;
			}
		}
		return true;
	}

	public void interactiveMode () {
		Scanner sc = new Scanner(System.in);
		System.out.printf("$ ");
		while (sc.hasNextLine()) {
			String[] args = {sc.nextLine()};
			perform(args);
			System.out.printf("$ ");
		}
	}
}