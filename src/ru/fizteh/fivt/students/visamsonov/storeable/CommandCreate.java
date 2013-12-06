package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;
import ru.fizteh.fivt.students.visamsonov.storage.StructuredTableDirectory;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class CommandCreate extends CommandAbstract<ShellState> {

	public CommandCreate () {
		super("create");
	}
	
	private void invalidFormat () {
		getErrStream().println("wrong type (invalid format)");
	}
	
	private void wrongType () {
		getErrStream().println("wrong type (unsupported)");
	}
	
	private void notEnoughArguments () {
		printError("not enough arguments");
	}

	public boolean evaluate (ShellState state, String args) {
		String[] argsArray = splitArguments(args);
		if (argsArray.length < 2) {
			notEnoughArguments();
			return false;
		}
		ArrayList<String> argsList = new ArrayList<String>();
		for (int i = 1; i < argsArray.length; i++) {
			argsList.add(argsArray[i]);
		}
		if (argsList.get(0).equals("(")) {
			argsList.remove(0);
		}
		else if (argsList.get(0).startsWith("(")) {
			argsList.set(0, argsList.get(0).substring(1));
		}
		else {
			invalidFormat();
			return false;
		}
		if (argsList.size() < 1) {
			notEnoughArguments();
			return false;
		}
		if (argsList.get(argsList.size() - 1).equals(")")) {
			argsList.remove(argsList.size() - 1);
		}
		else if (argsList.get(argsList.size() - 1).endsWith(")")) {
			int position = argsList.size() - 1;
			argsList.set(position, argsList.get(position).substring(0, argsList.get(position).length() - 1));
		}
		else {
			invalidFormat();
			return false;
		}
		if (argsList.size() < 1) {
			invalidFormat();
			return false;
		}
		List<Class<?>> types = new ArrayList<Class<?>>();
		for (int i = 0; i < argsList.size(); i++) {
			Class<?> type = StructuredTableDirectory.getTypeByName(argsList.get(i));
			if (type == null) {
				wrongType();
				return false;
			}
			types.add(type);
		}
		try {
			if (state.tableProvider.createTable(argsArray[0], types) == null) {
				getErrStream().println(argsArray[0] + " exists");
				return false;
			}
		}
		catch (IllegalArgumentException | IOException e) {
			getErrStream().println(e.getMessage());
			return false;
		}
		getOutStream().println("created");
		return true;
	}
}