package ru.fizteh.fivt.students.visamsonov.storeable;

import ru.fizteh.fivt.students.visamsonov.shell.CommandAbstract;
import ru.fizteh.fivt.storage.structured.Storeable;
import java.text.ParseException;

public class CommandPut extends CommandAbstract<ShellState> {

	public CommandPut () {
		super("put");
	}

	private String[] splitArgs (String args) {
		if (args == null || args.equals("")) {
			return new String[0];
		}
		return args.split("\\s+", 2);
	}
	
	public boolean evaluate (ShellState state, String args) {
		String[] argArray = splitArgs(args);
		if (!checkFixedArguments(argArray, 2)) {
			return false;
		}
		if (state.database == null) {
			printError("no table");
			return false;
		}
		try {
			Storeable oldValue = state.database.put(argArray[0], state.tableProvider.deserialize(state.database, argArray[1]));
			if (oldValue != null) {
				getOutStream().printf("overwrite\n%s\n", state.tableProvider.serialize(state.database, oldValue));
			}
			else {
				getOutStream().println("new");
			}
		}
		catch (ParseException e) {
			getErrStream().printf("wrong type %s\n", e.getMessage());
			return false;
		}
		return true;
	}
}