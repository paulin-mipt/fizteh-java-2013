package ru.fizteh.fivt.students.visamsonov.filehashmap;

import ru.fizteh.fivt.students.visamsonov.shell.Shell;
import ru.fizteh.fivt.students.visamsonov.util.TerminateRuntimeException;
import java.io.*;

public class DbMain {

	public static void main (String[] args) {
		Shell shell;
		try {
			shell = new Shell<ShellState>(new ShellState());
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}
		shell.addCommand(new CommandExit());
		shell.addCommand(new CommandPut());
		shell.addCommand(new CommandGet());
		shell.addCommand(new CommandRemove());
		shell.addCommand(new CommandCreate());
		shell.addCommand(new CommandUse());
		shell.addCommand(new CommandDrop());
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