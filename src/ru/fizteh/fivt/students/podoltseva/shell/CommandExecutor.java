package ru.fizteh.fivt.students.podoltseva.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
	private Map<String, Command> commandList = new HashMap<String, Command>();
	
	public CommandExecutor() {
		Command cd = new CommandCd();
		Command cp = new CommandCp();
		Command dir = new CommandDir();
		Command exit = new CommandExit();
		Command mkdir = new CommandMkdir();
		Command mv = new CommandMv();
		Command pwd = new CommandPwd();
		Command rm = new CommandRm();
		commandList.put(cd.getName(), cd);
		commandList.put(cp.getName(), cp);
		commandList.put(dir.getName(), dir);
		commandList.put(exit.getName(), exit);
		commandList.put(mkdir.getName(), mkdir);
		commandList.put(mv.getName(), mv);
		commandList.put(pwd.getName(), pwd);
		commandList.put(rm.getName(), rm);
	}
	
	public void execute(State state, String commandWithArgs) throws IOException {
		if (commandWithArgs.length() == 0) {
			return;
		}
		String[] commandAndArgs = commandWithArgs.split("\\s+");
		Command command = commandList.get(commandAndArgs[0]);
		if (command == null) {
			throw new IOException(commandAndArgs[0] + ": no such command.");
		}
		if (command.getArgsCount() != (commandAndArgs.length - 1)) {
			throw new IOException(commandAndArgs[0] + ": This command has different number of arguments.");
		}
		String[] args = new String[commandAndArgs.length - 1];
		for (int i = 1; i < commandAndArgs.length; ++i) {
			args[i - 1] = commandAndArgs[i];
		}
		command.execute(state, args);
	}

}
