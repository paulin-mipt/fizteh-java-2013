package ru.fizteh.fivt.students.visamsonov.filemap;

import ru.fizteh.fivt.students.visamsonov.shell.Shell;
import ru.fizteh.fivt.students.visamsonov.util.TerminateRuntimeException;
import java.io.*;

public class DbMain {

	public static void main (String[] args) {
		ShellState shellState;
		try {
			shellState = new ShellState();
		}
		catch (IOException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(1);
			return;
		}
		Shell shell = new Shell<ShellState>(shellState);
		shell.addCommand(new CommandExit());
		shell.addCommand(new CommandPut());
		shell.addCommand(new CommandGet());
		shell.addCommand(new CommandRemove());
		shell.addCommand(new CommandTalkToMe());
		if (args.length == 0) {
			shell.interactiveMode();
		}
		else if (!shell.perform(args)) {
			System.exit(1);
		}
		throw new TerminateRuntimeException();
	}
}