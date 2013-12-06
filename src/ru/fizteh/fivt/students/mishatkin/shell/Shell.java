package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.*;
import java.util.*;

/**
 * shell.java
 * shell
 *
 * Created by Vladimir Mishatkin on 9/21/13
 *
 */
public class Shell {
	public static void main(String args[]) {
		InputStream inputStream = System.in;
		CommandSource in = (args.length > 0) ? new BatchCommandSource(args) :
				new StandardInputCommandSource(new Scanner(inputStream));
		in.initCommands(getShellCommands());
		ShellReceiver receiver = new ShellReceiver(System.out, args.length == 0);
		ShellRunner runner = new ShellRunner(in);
		runner.run(receiver);
	}

	protected static <Receiver extends CommandReceiver> Collection<Command<Receiver>> getShellCommands() {
		List<Command<Receiver>> validCommands = new ArrayList<>();
		validCommands.add((Command<Receiver>) new CdCommand<ShellReceiver>(null));
		validCommands.add((Command<Receiver>) new CpCommand<ShellReceiver>(null));
		validCommands.add((Command<Receiver>) new DirCommand<ShellReceiver>(null));
		validCommands.add((Command<Receiver>) new ExitCommand<ShellReceiver>(null));
		validCommands.add((Command<Receiver>) new MkdirCommand(null));
		validCommands.add((Command<Receiver>) new MvCommand(null));
		validCommands.add((Command<Receiver>) new PwdCommand(null));
		validCommands.add((Command<Receiver>) new RmCommand(null));
//		validCommands.add(new CdCommand(null));
//		validCommands.add(new CpCommand(null));
//		validCommands.add(new DirCommand(null));
//		validCommands.add(new ExitCommand(null));
//		validCommands.add(new MkdirCommand(null));
//		validCommands.add(new MvCommand(null));
//		validCommands.add(new PwdCommand(null));
//		validCommands.add(new RmCommand(null));
		return validCommands;
	}
}
